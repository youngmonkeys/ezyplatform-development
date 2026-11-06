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

import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.signature.PackageSignatures.KEY_LIST_JWS_TYPE;

public final class PackageSigningKeyLists {

    public static final String FIELD_VERSION = "version";
    public static final String FIELD_ISSUED_AT = "issuedAt";
    public static final String FIELD_EXPIRES_AT = "expiresAt";
    public static final String FIELD_KEYS = "keys";
    public static final String FIELD_ROLE = "role";
    public static final String FIELD_PUBLIC_KEY = "publicKey";

    private PackageSigningKeyLists() {}

    public static String sign(
        KeyPair rootKeyPair,
        long version,
        long issuedAt,
        long expiresAt,
        Map<PackageSigningKeyRole, ? extends Collection<PublicKey>> keysByRole
    ) {
        PackageSigner.validateKeyPair(rootKeyPair);
        if (version <= 0 || expiresAt <= issuedAt) {
            throw new IllegalArgumentException("invalid version or expiration");
        }
        List<Map<String, Object>> keys = new ArrayList<>();
        for (PackageSigningKeyRole role : PackageSigningKeyRole.values()) {
            Collection<PublicKey> roleKeys = keysByRole.get(role);
            if (roleKeys == null) {
                continue;
            }
            for (PublicKey publicKey : roleKeys) {
                Map<String, Object> key = new LinkedHashMap<>();
                key.put(FIELD_ROLE, role.getValue());
                key.put(FIELD_PUBLIC_KEY, PackageSigningKeys.toBase64(publicKey));
                keys.add(key);
            }
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(FIELD_VERSION, version);
        payload.put(FIELD_ISSUED_AT, issuedAt);
        payload.put(FIELD_EXPIRES_AT, expiresAt);
        payload.put(FIELD_KEYS, keys);
        return PackageSignatures.signJws(rootKeyPair, KEY_LIST_JWS_TYPE, payload);
    }

    public static PackageSigningKeyList verify(
        String jws,
        Collection<PublicKey> rootKeys,
        long now
    ) {
        PackageSigningKeyList keyList = verifyIgnoreExpiration(jws, rootKeys);
        if (keyList.isExpired(now)) {
            throw new InvalidPackageSignatureException("keyListExpired");
        }
        return keyList;
    }

    public static PackageSigningKeyList verifyIgnoreExpiration(
        String jws,
        Collection<PublicKey> rootKeys
    ) {
        Map<?, ?> payload = PackageSignatures.verifyJws(
            jws,
            KEY_LIST_JWS_TYPE,
            PackageSigningKeys.toKeyIdMap(rootKeys)
        );
        long version = toLong(payload.get(FIELD_VERSION));
        long issuedAt = toLong(payload.get(FIELD_ISSUED_AT));
        long expiresAt = toLong(payload.get(FIELD_EXPIRES_AT));
        if (version <= 0 || expiresAt <= issuedAt) {
            throw new InvalidPackageSignatureException("invalidKeyList");
        }
        Object keys = payload.get(FIELD_KEYS);
        if (!(keys instanceof List)) {
            throw new InvalidPackageSignatureException("invalidKeyList");
        }
        Map<PackageSigningKeyRole, List<PublicKey>> keysByRole =
            new EnumMap<>(PackageSigningKeyRole.class);
        for (Object item : (List<?>) keys) {
            if (!(item instanceof Map)) {
                throw new InvalidPackageSignatureException("invalidKeyList");
            }
            Map<?, ?> key = (Map<?, ?>) item;
            Object roleValue = key.get(FIELD_ROLE);
            Object publicKeyValue = key.get(FIELD_PUBLIC_KEY);
            PackageSigningKeyRole role = roleValue instanceof String
                ? PackageSigningKeyRole.of((String) roleValue)
                : null;
            if (role == null || !(publicKeyValue instanceof String)) {
                continue;
            }
            PublicKey publicKey;
            try {
                publicKey = PackageSigningKeys.decodePublicKey(
                    Base64.getDecoder().decode((String) publicKeyValue)
                );
            } catch (IllegalArgumentException e) {
                throw new InvalidPackageSignatureException("invalidKeyList", e);
            }
            keysByRole
                .computeIfAbsent(role, k -> new ArrayList<>())
                .add(publicKey);
        }
        Map<PackageSigningKeyRole, List<PublicKey>> unmodifiable =
            new EnumMap<>(PackageSigningKeyRole.class);
        for (Map.Entry<PackageSigningKeyRole, List<PublicKey>> e : keysByRole.entrySet()) {
            unmodifiable.put(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return new PackageSigningKeyList(
            version,
            issuedAt,
            expiresAt,
            Collections.unmodifiableMap(unmodifiable),
            jws.trim()
        );
    }

    private static long toLong(Object value) {
        if (value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue();
        }
        throw new InvalidPackageSignatureException("invalidKeyList");
    }
}
