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

package org.youngmonkeys.ezyplatform.security;

import com.tvd12.ezyfox.security.EzySHA256;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

final class SecureRandomHashGenerators {

    private static final int RANDOM_BYTES_LENGTH = 32;
    private static final char[] HEX_DIGITS = "0123456789abcdef"
        .toCharArray();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecureRandomHashGenerators() {}

    public static String generate(
        String purpose,
        String... values
    ) {
        byte[] randomBytes = new byte[RANDOM_BYTES_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);

        MessageDigest messageDigest = newMessageDigest();
        update(messageDigest, purpose);
        for (String value : values) {
            update(messageDigest, value);
        }
        messageDigest.update(randomBytes);
        return toLowercaseHex(messageDigest.digest());
    }

    private static MessageDigest newMessageDigest() {
        try {
            return MessageDigest.getInstance(EzySHA256.ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                "SHA-256 is unavailable",
                e
            );
        }
    }

    private static void update(
        MessageDigest messageDigest,
        String value
    ) {
        byte[] bytes = String
            .valueOf(value)
            .getBytes(StandardCharsets.UTF_8);
        messageDigest.update(
            ByteBuffer
                .allocate(Integer.BYTES)
                .putInt(bytes.length)
                .array()
        );
        messageDigest.update(bytes);
    }

    private static String toLowercaseHex(byte[] bytes) {
        char[] answer = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; ++i) {
            int value = bytes[i] & 0xFF;
            answer[i * 2] = HEX_DIGITS[value >>> 4];
            answer[i * 2 + 1] = HEX_DIGITS[value & 0x0F];
        }
        return new String(answer);
    }
}
