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

import org.youngmonkeys.ezyplatform.util.Hexes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PackageSigningKeys {

    public static final String KEY_ALGORITHM = "EC";
    public static final String CURVE_NAME = "secp256r1";
    public static final int CURVE_FIELD_SIZE = 256;
    public static final String KEY_ID_PREFIX = "ezy-";
    public static final String PEM_PRIVATE_KEY = "PRIVATE KEY";
    public static final String PEM_PUBLIC_KEY = "PUBLIC KEY";

    private static final int KEY_ID_HEX_LENGTH = 16;
    private static final int PEM_LINE_LENGTH = 64;
    private static final Pattern PEM_BLOCK_PATTERN = Pattern.compile(
        "-----BEGIN ([A-Z ]+)-----([A-Za-z0-9+/=\\s]+)-----END \\1-----"
    );

    private PackageSigningKeys() {}

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(
            KEY_ALGORITHM
        );
        generator.initialize(new ECGenParameterSpec(CURVE_NAME));
        return generator.generateKeyPair();
    }

    public static String keyIdOf(PublicKey publicKey) {
        try {
            byte[] digest = MessageDigest
                .getInstance("SHA-256")
                .digest(publicKey.getEncoded());
            return KEY_ID_PREFIX + Hexes
                .toLowercaseHex(digest)
                .substring(0, KEY_ID_HEX_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SHA-256 is not supported", e);
        }
    }

    public static String toBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static List<PublicKey> parseBase64PublicKeys(String value) {
        List<PublicKey> answer = new ArrayList<>();
        if (value == null) {
            return answer;
        }
        for (String item : value.split("[,\\s]+")) {
            if (!item.isEmpty()) {
                answer.add(decodePublicKey(Base64.getDecoder().decode(item)));
            }
        }
        return answer;
    }

    public static String toPem(PrivateKey privateKey) {
        return toPem(PEM_PRIVATE_KEY, privateKey.getEncoded());
    }

    public static String toPem(PublicKey publicKey) {
        return toPem(PEM_PUBLIC_KEY, publicKey.getEncoded());
    }

    public static String toPem(KeyPair keyPair) {
        return toPem(keyPair.getPrivate()) + toPem(keyPair.getPublic());
    }

    private static String toPem(String type, byte[] encoded) {
        String base64 = Base64.getEncoder().encodeToString(encoded);
        StringBuilder builder = new StringBuilder()
            .append("-----BEGIN ").append(type).append("-----\n");
        for (int i = 0; i < base64.length(); i += PEM_LINE_LENGTH) {
            builder
                .append(
                    base64,
                    i,
                    Math.min(base64.length(), i + PEM_LINE_LENGTH)
                )
                .append('\n');
        }
        return builder
            .append("-----END ").append(type).append("-----\n")
            .toString();
    }

    public static PrivateKey parsePrivateKey(String pem) {
        List<byte[]> blocks = readPemBlocks(pem, PEM_PRIVATE_KEY);
        if (blocks.size() != 1) {
            throw new IllegalArgumentException(
                "expected exactly one private key"
            );
        }
        return decodePrivateKey(blocks.get(0));
    }

    public static List<PublicKey> parsePublicKeys(String pem) {
        List<PublicKey> answer = new ArrayList<>();
        for (byte[] block : readPemBlocks(pem, PEM_PUBLIC_KEY)) {
            answer.add(decodePublicKey(block));
        }
        return answer;
    }

    public static PublicKey parsePublicKey(String pem) {
        List<PublicKey> keys = parsePublicKeys(pem);
        if (keys.size() != 1) {
            throw new IllegalArgumentException(
                "expected exactly one public key"
            );
        }
        return keys.get(0);
    }

    public static KeyPair parseKeyPair(String pem) {
        return new KeyPair(parsePublicKey(pem), parsePrivateKey(pem));
    }

    public static KeyPair readKeyPair(File file) throws IOException {
        return parseKeyPair(readText(file));
    }

    public static List<PublicKey> readPublicKeys(File file) throws IOException {
        return parsePublicKeys(readText(file));
    }

    public static Map<String, PublicKey> toKeyIdMap(
        Collection<PublicKey> publicKeys
    ) {
        Map<String, PublicKey> answer = new LinkedHashMap<>();
        for (PublicKey publicKey : publicKeys) {
            answer.put(keyIdOf(publicKey), publicKey);
        }
        return Collections.unmodifiableMap(answer);
    }

    public static PrivateKey decodePrivateKey(byte[] encoded) {
        try {
            PrivateKey key = KeyFactory
                .getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(encoded));
            validateCurve(key);
            return key;
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("invalid private key", e);
        }
    }

    public static PublicKey decodePublicKey(byte[] encoded) {
        try {
            PublicKey key = KeyFactory
                .getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(encoded));
            validateCurve(key);
            return key;
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("invalid public key", e);
        }
    }

    private static void validateCurve(Object key) {
        if (!(key instanceof ECKey)) {
            throw new IllegalArgumentException("key is not an EC key");
        }
        int fieldSize = ((ECKey) key)
            .getParams()
            .getCurve()
            .getField()
            .getFieldSize();
        if (fieldSize != CURVE_FIELD_SIZE) {
            throw new IllegalArgumentException(
                "key is not a P-256 key"
            );
        }
    }

    private static List<byte[]> readPemBlocks(
        String pem,
        String type
    ) {
        List<byte[]> answer = new ArrayList<>();
        if (pem == null) {
            return answer;
        }
        Matcher matcher = PEM_BLOCK_PATTERN.matcher(pem);
        while (matcher.find()) {
            if (type.equals(matcher.group(1))) {
                answer.add(
                    Base64.getMimeDecoder().decode(matcher.group(2))
                );
            }
        }
        return answer;
    }

    private static String readText(File file) throws IOException {
        return new String(
            Files.readAllBytes(file.toPath()),
            StandardCharsets.UTF_8
        );
    }
}
