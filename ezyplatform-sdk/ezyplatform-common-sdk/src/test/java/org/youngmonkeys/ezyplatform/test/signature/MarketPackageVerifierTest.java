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

package org.youngmonkeys.ezyplatform.test.signature;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;
import org.youngmonkeys.ezyplatform.signature.MarketPackageVerifier;
import org.youngmonkeys.ezyplatform.signature.PackageManifest;
import org.youngmonkeys.ezyplatform.signature.PackageSigner;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeyList;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeyLists;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeyRole;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeys;
import org.youngmonkeys.ezyplatform.signature.PackageType;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarketPackageVerifierTest {

    private static final long DAY = 24L * 60 * 60 * 1000;
    private static final String MARKET_URL = "https://ezyplatform.com";

    private KeyPair rootKeyPair;
    private KeyPair marketKeyPair;
    private KeyPair platformKeyPair;
    private HttpClient httpClient;
    private File cacheFolder;
    private AtomicLong now;
    private Map<String, Object> responseByUrl;

    @BeforeMethod
    public void setup() throws Exception {
        rootKeyPair = PackageSigningKeys.generateKeyPair();
        marketKeyPair = PackageSigningKeys.generateKeyPair();
        platformKeyPair = PackageSigningKeys.generateKeyPair();
        cacheFolder = Files.createTempDirectory("market-package-verifier").toFile();
        cacheFolder.deleteOnExit();
        now = new AtomicLong(1000L * DAY);
        responseByUrl = new HashMap<>();
        httpClient = mock(HttpClient.class);
        when(httpClient.call(any(Request.class))).thenAnswer(it -> {
            Request request = it.getArgument(0);
            Object response = responseByUrl.get(request.getURL());
            if (response == null) {
                throw new IllegalStateException("not found: " + request.getURL());
            }
            return response;
        });
    }

    @Test
    public void fetchModuleManifestTest() {
        // given
        serveKeyList(1L, now.get() + DAY);
        serveSignature(
            "/api/v1/released-projects/ezyarticle/latest/signature",
            new PackageSigner(marketKeyPair).sign(manifest("ezyarticle", "1.2.3"))
        );

        // when
        PackageManifest actual = newVerifier().fetchManifest(
            MARKET_URL + "/",
            "/api/v1/released-projects/ezyarticle/latest/signature",
            PackageType.MODULE,
            "ezyarticle",
            "latest"
        );

        // then
        Asserts.assertEquals(actual.getVersion(), "1.2.3");
    }

    @Test
    public void moduleSignedByPlatformKeyIsRejectedTest() {
        // given
        serveKeyList(1L, now.get() + DAY);
        serveSignature(
            "/api/v1/released-projects/ezyarticle/1.2.3/signature",
            new PackageSigner(platformKeyPair).sign(manifest("ezyarticle", "1.2.3"))
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            newVerifier().fetchManifest(
                MARKET_URL,
                "/api/v1/released-projects/ezyarticle/1.2.3/signature",
                PackageType.MODULE,
                "ezyarticle",
                "1.2.3"
            )
        );

        // then
        assertReason(e, "untrustedKey");
    }

    @Test
    public void keyListSignedByUnknownRootIsRejectedTest() throws Exception {
        // given
        KeyPair attackerRoot = PackageSigningKeys.generateKeyPair();
        responseByUrl.put(
            MARKET_URL + "/api/v1/package-signing-keys",
            Collections.singletonMap(
                "keys",
                signKeyList(attackerRoot, 1L, now.get() + DAY)
            )
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            newVerifier().getKeyList(MARKET_URL)
        );

        // then
        assertReason(e, "untrustedKey");
    }

    @Test
    public void cachedKeyListIsUsedWhenMarketIsUnavailableTest() {
        // given
        serveKeyList(2L, now.get() + DAY);
        newVerifier().getKeyList(MARKET_URL);
        responseByUrl.clear();

        // when
        PackageSigningKeyList actual = newVerifier().getKeyList(MARKET_URL);

        // then
        Asserts.assertEquals(actual.getVersion(), 2L);
    }

    @Test
    public void expiredCachedKeyListIsRejectedWhenMarketIsUnavailableTest() {
        // given
        serveKeyList(2L, now.get() + DAY);
        newVerifier().getKeyList(MARKET_URL);
        responseByUrl.clear();
        now.addAndGet(2 * DAY);

        // when
        Throwable e = Asserts.assertThrows(() ->
            newVerifier().getKeyList(MARKET_URL)
        );

        // then
        assertReason(e, "keyListUnavailable");
    }

    @Test
    public void olderKeyListIsRejectedInFavorOfCachedTest() {
        // given
        serveKeyList(5L, now.get() + DAY);
        newVerifier().getKeyList(MARKET_URL);
        serveKeyList(3L, now.get() + DAY);

        // when
        PackageSigningKeyList actual = newVerifier().getKeyList(MARKET_URL);

        // then
        Asserts.assertEquals(actual.getVersion(), 5L);
    }

    @Test
    public void unknownMarketIsRejectedTest() {
        // given
        MarketPackageVerifier verifier = newVerifier();

        // when
        Throwable e = Asserts.assertThrows(() ->
            verifier.getKeyList("https://market.example.com")
        );

        // then
        assertReason(e, "untrustedMarket");
    }

    @Test
    public void customMarketRootKeysTest() throws Exception {
        // given
        KeyPair customRoot = PackageSigningKeys.generateKeyPair();
        String value = "https://market.example.com/ " +
            PackageSigningKeys.toBase64(customRoot.getPublic());
        Map<String, List<PublicKey>> customRoots =
            MarketPackageVerifier.parseMarketRootPublicKeys(value);
        responseByUrl.put(
            "https://market.example.com/api/v1/package-signing-keys",
            Collections.singletonMap(
                "keys",
                signKeyList(customRoot, 1L, now.get() + DAY)
            )
        );
        MarketPackageVerifier verifier = new MarketPackageVerifier(
            httpClient,
            cacheFolder,
            Collections.singletonList(rootKeyPair.getPublic()),
            customRoots,
            now::get
        );

        // when
        PackageSigningKeyList actual = verifier.getKeyList(
            "https://market.example.com"
        );

        // then
        Asserts.assertEquals(actual.getVersion(), 1L);
    }

    private MarketPackageVerifier newVerifier() {
        return new MarketPackageVerifier(
            httpClient,
            cacheFolder,
            Collections.singletonList(rootKeyPair.getPublic()),
            Collections.emptyMap(),
            now::get
        );
    }

    private void serveKeyList(long version, long expiresAt) {
        responseByUrl.put(
            MARKET_URL + "/api/v1/package-signing-keys",
            Collections.singletonMap(
                "keys",
                signKeyList(rootKeyPair, version, expiresAt)
            )
        );
    }

    private String signKeyList(KeyPair root, long version, long expiresAt) {
        Map<PackageSigningKeyRole, List<PublicKey>> keys =
            new EnumMap<>(PackageSigningKeyRole.class);
        keys.put(
            PackageSigningKeyRole.MARKET,
            Collections.singletonList(marketKeyPair.getPublic())
        );
        keys.put(
            PackageSigningKeyRole.PLATFORM,
            Collections.singletonList(platformKeyPair.getPublic())
        );
        return PackageSigningKeyLists.sign(
            root,
            version,
            now.get(),
            expiresAt,
            keys
        );
    }

    private void serveSignature(String path, String signature) {
        responseByUrl.put(
            MARKET_URL + path,
            Collections.singletonMap("signature", signature)
        );
    }

    private static PackageManifest manifest(String name, String version) {
        return PackageManifest.builder()
            .type(PackageType.MODULE)
            .name(name)
            .version(version)
            .fileName(name + "-" + version + ".zip")
            .size(10L)
            .sha256("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
            .issuedAt(1L)
            .build();
    }

    private static void assertReason(Throwable e, String reason) {
        Asserts.assertEqualsType(e, InvalidPackageSignatureException.class);
        Asserts.assertEquals(
            ((InvalidPackageSignatureException) e).getReason(),
            reason
        );
    }
}
