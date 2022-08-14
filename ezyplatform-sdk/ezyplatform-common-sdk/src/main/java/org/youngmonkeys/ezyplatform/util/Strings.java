/*
 * Copyright 2022 youngmonkeys.org
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

import java.util.Map;

public final class Strings {

    private Strings() {}

    public static String from(Object value) {
        return value != null ? value.toString() : null;
    }

    public static String entryToString(Map.Entry<?, ?> entry) {
        String key = entry.getKey().toString();
        String value = entry.getValue() != null
            ? entry.getValue().toString()
            : null;
        return value != null
            ? key.trim() + "=" + value.trim()
            : key.trim() + "=";
    }

    public static boolean startsWithIgnoreSpaces(String str, String prefix) {
        int i = 0;
        for (; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch != ' ' && ch != '\t') {
                break;
            }
        }
        int k = 0;
        for (; k < prefix.length() && i < str.length(); ++k, ++i) {
            if (prefix.charAt(k) != str.charAt(i)) {
                return false;
            }
        }
        return k >= prefix.length();
    }
}
