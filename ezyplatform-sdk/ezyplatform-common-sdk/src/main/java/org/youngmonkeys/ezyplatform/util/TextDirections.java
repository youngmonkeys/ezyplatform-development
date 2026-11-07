/*
 * Copyright 2025 youngmonkeys.org
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class TextDirections {

    public static final String LTR = "ltr";
    public static final String RTL = "rtl";

    private static final Set<String> RTL_LANGUAGE_CODES =
        Collections.unmodifiableSet(
            new HashSet<>(
                Arrays.asList(
                    "ar",
                    "arc",
                    "ckb",
                    "dv",
                    "fa",
                    "he",
                    "iw",
                    "ku",
                    "ps",
                    "sd",
                    "ug",
                    "ur",
                    "yi"
                )
            )
        );

    private TextDirections() {}

    public static boolean isValidTextDirection(String direction) {
        return LTR.equals(direction) || RTL.equals(direction);
    }

    public static boolean isRtlLanguage(String languageCode) {
        if (isBlank(languageCode)) {
            return false;
        }
        String code = languageCode.trim().toLowerCase(Locale.ENGLISH);
        int separatorIndex = indexOfSeparator(code);
        if (separatorIndex > 0) {
            code = code.substring(0, separatorIndex);
        }
        return RTL_LANGUAGE_CODES.contains(code);
    }

    public static String getTextDirection(String languageCode) {
        return isRtlLanguage(languageCode) ? RTL : LTR;
    }

    public static String getTextDirection(
        String languageCode,
        String configuredDirection
    ) {
        if (isBlank(configuredDirection)) {
            return getTextDirection(languageCode);
        }
        String direction = configuredDirection
            .trim()
            .toLowerCase(Locale.ENGLISH);
        return isValidTextDirection(direction)
            ? direction
            : getTextDirection(languageCode);
    }

    private static int indexOfSeparator(String code) {
        for (int i = 0; i < code.length(); ++i) {
            char ch = code.charAt(i);
            if (ch == '-' || ch == '_') {
                return i;
            }
        }
        return -1;
    }
}
