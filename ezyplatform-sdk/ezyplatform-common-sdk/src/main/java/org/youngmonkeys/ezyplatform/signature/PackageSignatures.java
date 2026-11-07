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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PackageSignatures {

    public static final String JWS_ALGORITHM = "ES256";
    public static final String JWS_TYPE = "ezy-package+jws";
    public static final String KEY_LIST_JWS_TYPE = "ezy-signing-keys+jws";
    public static final String JWS_HEADER_ALGORITHM = "alg";
    public static final String JWS_HEADER_KEY_ID = "kid";
    public static final String JWS_HEADER_TYPE = "typ";
    public static final String JWS_HEADER_CRITICAL = "crit";
    public static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    public static final int SIGNATURE_COMPONENT_LENGTH = 32;
    public static final int MAX_JWS_LENGTH = 65536;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private PackageSignatures() {}

    static String signJws(
        KeyPair keyPair,
        String type,
        Map<String, Object> payload
    ) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put(JWS_HEADER_ALGORITHM, JWS_ALGORITHM);
        header.put(JWS_HEADER_KEY_ID, PackageSigningKeys.keyIdOf(keyPair.getPublic()));
        header.put(JWS_HEADER_TYPE, type);
        try {
            String encodedHeader = base64UrlEncode(
                OBJECT_MAPPER.writeValueAsBytes(header)
            );
            String encodedPayload = base64UrlEncode(
                OBJECT_MAPPER.writeValueAsBytes(payload)
            );
            byte[] signature = signBytes(
                keyPair.getPrivate(),
                signingInput(encodedHeader, encodedPayload)
            );
            return encodedHeader + "." +
                encodedPayload + "." +
                base64UrlEncode(signature);
        } catch (Exception e) {
            throw new IllegalStateException("can not sign jws", e);
        }
    }

    static Map<?, ?> verifyJws(
        String jws,
        String expectedType,
        Map<String, PublicKey> trustedKeyById
    ) {
        if (jws == null) {
            throw new InvalidPackageSignatureException("invalidFormat");
        }
        String trimmed = jws.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_JWS_LENGTH) {
            throw new InvalidPackageSignatureException("invalidFormat");
        }
        String[] parts = trimmed.split("\\.", -1);
        if (parts.length != 3) {
            throw new InvalidPackageSignatureException("invalidFormat");
        }
        Map<?, ?> header = decodeJson(parts[0]);
        if (!JWS_ALGORITHM.equals(header.get(JWS_HEADER_ALGORITHM))) {
            throw new InvalidPackageSignatureException("unsupportedAlgorithm");
        }
        if (!expectedType.equals(header.get(JWS_HEADER_TYPE))) {
            throw new InvalidPackageSignatureException("unsupportedType");
        }
        if (header.containsKey(JWS_HEADER_CRITICAL)) {
            throw new InvalidPackageSignatureException("unsupportedCritical");
        }
        Object keyId = header.get(JWS_HEADER_KEY_ID);
        PublicKey publicKey = keyId instanceof String
            ? trustedKeyById.get(keyId)
            : null;
        if (publicKey == null) {
            throw new InvalidPackageSignatureException("untrustedKey");
        }
        if (!verifySignature(publicKey, parts)) {
            throw new InvalidPackageSignatureException("invalidSignature");
        }
        return decodeJson(parts[1]);
    }

    static byte[] signBytes(
        PrivateKey privateKey,
        byte[] data
    ) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return EcdsaSignatureFormats.derToConcatenated(
            signature.sign(),
            SIGNATURE_COMPONENT_LENGTH
        );
    }

    static boolean verifyBytes(
        PublicKey publicKey,
        byte[] data,
        byte[] concatenatedSignature
    ) {
        try {
            if (concatenatedSignature.length != SIGNATURE_COMPONENT_LENGTH * 2) {
                return false;
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(
                EcdsaSignatureFormats.concatenatedToDer(concatenatedSignature)
            );
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean verifySignature(
        PublicKey publicKey,
        String[] parts
    ) {
        byte[] signature;
        try {
            signature = base64UrlDecode(parts[2]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return verifyBytes(
            publicKey,
            signingInput(parts[0], parts[1]),
            signature
        );
    }

    private static Map<?, ?> decodeJson(String encoded) {
        try {
            Object value = OBJECT_MAPPER.readValue(
                base64UrlDecode(encoded),
                Object.class
            );
            if (value instanceof Map) {
                return (Map<?, ?>) value;
            }
        } catch (Exception e) {
            throw new InvalidPackageSignatureException("invalidFormat", e);
        }
        throw new InvalidPackageSignatureException("invalidFormat");
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String text) {
        return Base64.getUrlDecoder().decode(text);
    }

    private static byte[] signingInput(
        String encodedHeader,
        String encodedPayload
    ) {
        return (encodedHeader + "." + encodedPayload)
            .getBytes(StandardCharsets.US_ASCII);
    }
}
