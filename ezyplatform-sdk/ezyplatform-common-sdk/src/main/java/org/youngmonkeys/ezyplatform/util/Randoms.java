/*
 * Copyright 2023 youngmonkeys.org
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

import java.security.SecureRandom;

public final class Randoms {

    private static final int DISPLAY_NAME_LENGTH = 16;
    private static final String DISPLAY_NAME_CHARACTERS =
        "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DISPLAY_NAME_CHARACTER_LENGTH =
        DISPLAY_NAME_CHARACTERS.length();

    private Randoms() {}

    public static String randomName() {
        return randomName(
            DISPLAY_NAME_LENGTH
        );
    }

    public static String randomName(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(
                DISPLAY_NAME_CHARACTERS.charAt(
                    random.nextInt(
                        DISPLAY_NAME_CHARACTER_LENGTH
                    )
                )
            );
        }
        return sb.toString();
    }
}
