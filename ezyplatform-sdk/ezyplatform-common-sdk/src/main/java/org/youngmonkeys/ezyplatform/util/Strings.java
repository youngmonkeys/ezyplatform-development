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

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class Strings {

    private Strings() {}

    public static String from(Object value) {
        return value != null ? value.toString() : null;
    }

    public static String toLowerDashCase(String str) {
        return isBlank(str)
            ? str
            : str.replace(' ', '-').toLowerCase();
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

    public static boolean containInvalidSpaces(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '\n' || ch == '\t') {
                return true;
            }
            if (ch == ' '
                && i < str.length() - 1
                && str.charAt(i + 1) == ' '
            ) {
                return true;
            }
        }
        return false;
    }

    public static String hideSensitiveInformation(
        String str,
        int startHidden,
        int hiddenLength
    ) {
        if (startHidden >= str.length()) {
            return str;
        }
        StringBuilder builder = new StringBuilder()
            .append(str, 0, startHidden)
            .append("***");
        int end = startHidden + hiddenLength;
        if (end < str.length() - 1) {
            builder.append(str.substring(end));
        }
        return builder.toString();
    }

    public static String escapeScriptTag(String content) {
        return content
            .replace("<script>", "&lt;script&gt;")
            .replace("</script>", "&lt;/script&gt;");
    }

    public static String emptyIfNull(String str) {
        return str != null ? str : EMPTY_STRING;
    }
}
