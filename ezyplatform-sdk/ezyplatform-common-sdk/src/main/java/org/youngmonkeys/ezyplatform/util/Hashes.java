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

package org.youngmonkeys.ezyplatform.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hashes {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SHA256 = "SHA-256";
    private static final int FILE_BUFFER_SIZE = 8192;

    private Hashes() {}

    public static String sha256Hex(File file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not supported", e);
        }
        try (InputStream input = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                digest.update(buffer, 0, length);
            }
        }
        return Hexes.toLowercaseHex(digest.digest());
    }

    public static String hmacSha256(
        String key,
        String data
    ) throws Exception {
        if (key == null) {
            throw new NullPointerException(
                "hmacSha256 key is null"
            );
        }
        if (data == null) {
            throw new NullPointerException(
                "hmacSha256 data is null"
            );
        }
        final Mac hmac256 = Mac.getInstance(HMAC_SHA256);
        byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
        final SecretKeySpec secretKey = new SecretKeySpec(
            hmacKeyBytes,
            HMAC_SHA256
        );
        hmac256.init(secretKey);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] result = hmac256.doFinal(dataBytes);
        StringBuilder sb = new StringBuilder(2 * result.length);
        for (byte b : result) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
