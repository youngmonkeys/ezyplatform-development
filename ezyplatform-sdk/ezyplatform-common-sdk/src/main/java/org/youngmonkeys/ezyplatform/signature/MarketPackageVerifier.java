/*
 * Copyright 2026 youngmonkeys.org
 *
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.signature;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.GetRequest;
import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;
import org.youngmonkeys.ezyplatform.util.Hexes;
import org.youngmonkeys.ezyplatform.util.Uris;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

public class MarketPackageVerifier {

    private final HttpClient httpClient;
    private final File cacheFolder;
    private final Map<String, List<PublicKey>> rootKeysByMarketUrl;
    private final LongSupplier clock;

    public static final String EZYPLATFORM_MARKET_URL = "https://ezyplatform.com";
    public static final String PROPERTY_ROOT_PUBLIC_KEYS =
        "package_signing.root_public_keys";
    public static final String PROPERTY_MARKET_ROOT_PUBLIC_KEYS =
        "market.signing_root_public_keys";
    public static final String KEY_LIST_PATH = "/api/v1/package-signing-keys";
    public static final String FIELD_KEYS = "keys";
    public static final String FIELD_SIGNATURE = "signature";

    public MarketPackageVerifier(
        HttpClient httpClient,
        File cacheFolder,
        List<PublicKey> ezyplatformRootKeys,
        Map<String, List<PublicKey>> customRootKeysByMarketUrl
    ) {
        this(
            httpClient,
            cacheFolder,
            ezyplatformRootKeys,
            customRootKeysByMarketUrl,
            System::currentTimeMillis
        );
    }

    public MarketPackageVerifier(
        HttpClient httpClient,
        File cacheFolder,
        List<PublicKey> ezyplatformRootKeys,
        Map<String, List<PublicKey>> customRootKeysByMarketUrl,
        LongSupplier clock
    ) {
        this.httpClient = httpClient;
        this.cacheFolder = cacheFolder;
        this.clock = clock;
        Map<String, List<PublicKey>> map = new HashMap<>();
        for (Map.Entry<String, List<PublicKey>> e : customRootKeysByMarketUrl.entrySet()) {
            map.put(normalizeMarketUrl(e.getKey()), e.getValue());
        }
        map.put(EZYPLATFORM_MARKET_URL, ezyplatformRootKeys);
        this.rootKeysByMarketUrl = Collections.unmodifiableMap(map);
    }

    public static Map<String, List<PublicKey>> parseMarketRootPublicKeys(
        String value
    ) {
        Map<String, List<PublicKey>> answer = new HashMap<>();
        if (value == null || value.trim().isEmpty()) {
            return answer;
        }
        for (String entry : value.split(",")) {
            String[] tokens = entry.trim().split("\\s+");
            if (tokens.length < 2) {
                continue;
            }
            List<PublicKey> keys = new ArrayList<>();
            for (int i = 1; i < tokens.length; ++i) {
                keys.addAll(PackageSigningKeys.parseBase64PublicKeys(tokens[i]));
            }
            answer
                .computeIfAbsent(normalizeMarketUrl(tokens[0]), k -> new ArrayList<>())
                .addAll(keys);
        }
        return answer;
    }

    public PackageManifest fetchManifest(
        String marketUrl,
        String signaturePath,
        PackageType type,
        String name,
        String requestedVersion
    ) {
        PackageSigningKeyList keyList = getKeyList(marketUrl);
        String signature = fetchSignature(
            Uris.resolveUrl(normalizeMarketUrl(marketUrl), signaturePath)
        );
        return keyList
            .getVerifier(type)
            .verify(signature, type, name, requestedVersion);
    }

    public PackageSigningKeyList getKeyList(String marketUrl) {
        String normalizedMarketUrl = normalizeMarketUrl(marketUrl);
        List<PublicKey> rootKeys = rootKeysByMarketUrl.get(normalizedMarketUrl);
        if (rootKeys == null || rootKeys.isEmpty()) {
            throw new InvalidPackageSignatureException("untrustedMarket");
        }
        long now = clock.getAsLong();
        File cacheFile = getCacheFile(normalizedMarketUrl);
        PackageSigningKeyList cached = readCachedKeyList(cacheFile, rootKeys);
        PackageSigningKeyList remote = null;
        InvalidPackageSignatureException remoteError = null;
        try {
            remote = PackageSigningKeyLists.verify(
                fetchKeyList(normalizedMarketUrl),
                rootKeys,
                now
            );
        } catch (InvalidPackageSignatureException e) {
            remoteError = e;
        }
        if (remote != null) {
            if (cached != null && remote.getVersion() < cached.getVersion()) {
                if (!cached.isExpired(now)) {
                    return cached;
                }
                throw new InvalidPackageSignatureException("keyListRollback");
            }
            writeCachedKeyList(cacheFile, remote);
            return remote;
        }
        if (cached != null && !cached.isExpired(now)) {
            return cached;
        }
        throw remoteError;
    }

    private String fetchKeyList(String marketUrl) {
        return fetchString(
            Uris.resolveUrl(marketUrl, KEY_LIST_PATH),
            FIELD_KEYS,
            "keyListUnavailable"
        );
    }

    private String fetchSignature(String url) {
        return fetchString(url, FIELD_SIGNATURE, "signatureUnavailable");
    }

    private String fetchString(
        String url,
        String field,
        String errorReason
    ) {
        Object value;
        try {
            Map<?, ?> response = httpClient.call(
                new GetRequest()
                    .setURL(url)
                    .setResponseType(Map.class)
            );
            value = response != null ? response.get(field) : null;
        } catch (Exception e) {
            throw new InvalidPackageSignatureException(errorReason, e);
        }
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new InvalidPackageSignatureException(errorReason);
        }
        return (String) value;
    }

    private PackageSigningKeyList readCachedKeyList(
        File cacheFile,
        List<PublicKey> rootKeys
    ) {
        if (!cacheFile.isFile()) {
            return null;
        }
        try {
            return PackageSigningKeyLists.verifyIgnoreExpiration(
                new String(
                    Files.readAllBytes(cacheFile.toPath()),
                    StandardCharsets.US_ASCII
                ),
                rootKeys
            );
        } catch (Exception e) {
            return null;
        }
    }

    private void writeCachedKeyList(
        File cacheFile,
        PackageSigningKeyList keyList
    ) {
        try {
            File parent = cacheFile.getParentFile();
            if (parent != null) {
                Files.createDirectories(parent.toPath());
            }
            File tempFile = new File(cacheFile.getPath() + ".tmp");
            Files.write(
                tempFile.toPath(),
                keyList.getJws().getBytes(StandardCharsets.US_ASCII)
            );
            Files.move(
                tempFile.toPath(),
                cacheFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException expected) {
        }
    }

    private File getCacheFile(String marketUrl) {
        try {
            byte[] digest = MessageDigest
                .getInstance("SHA-256")
                .digest(marketUrl.getBytes(StandardCharsets.UTF_8));
            return new File(
                cacheFolder,
                "signing-keys-" + Hexes.toLowercaseHex(digest).substring(0, 16) + ".jws"
            );
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is not supported", e);
        }
    }

    private static String normalizeMarketUrl(String marketUrl) {
        return Uris.normalizeUrl(marketUrl.trim());
    }
}
