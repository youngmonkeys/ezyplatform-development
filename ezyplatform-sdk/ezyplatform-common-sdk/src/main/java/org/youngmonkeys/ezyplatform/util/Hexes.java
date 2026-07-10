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

public final class Hexes {

    private static final char[] LOWERCASE_HEX_DIGITS =
        "0123456789abcdef".toCharArray();

    private Hexes() {}

    public static String toLowercaseHex(byte[] bytes) {
        char[] answer = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; ++i) {
            int value = bytes[i] & 0xFF;
            answer[i * 2] = LOWERCASE_HEX_DIGITS[value >>> 4];
            answer[i * 2 + 1] = LOWERCASE_HEX_DIGITS[value & 0x0F];
        }
        return new String(answer);
    }

    public static byte[] decodeLowercaseHex(String text) {
        int length = text.length();
        if ((length & 1) != 0) {
            throw new IllegalArgumentException("invalid hex length");
        }
        byte[] answer = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            answer[i / 2] = (byte) (
                (hexDigitToInt(text.charAt(i)) << 4) +
                    hexDigitToInt(text.charAt(i + 1))
                );
        }
        return answer;
    }

    private static int hexDigitToInt(char digit) {
        if (digit >= '0' && digit <= '9') {
            return digit - '0';
        }
        if (digit >= 'a' && digit <= 'f') {
            return digit - 'a' + 10;
        }
        throw new IllegalArgumentException(
            "invalid hex digit: " + digit
        );
    }
}
