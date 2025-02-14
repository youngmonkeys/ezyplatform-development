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

import com.tvd12.ezyfox.io.EzyDates;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.*;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public final class Strings {

    public static final String SPECIAL_CHARACTERS =
        "!\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";

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

    public static String substring(String str, int from, int to) {
        return str.substring(
            Math.max(from, 0),
            Math.min(to, str.length())
        );
    }

    public static String substringLast(
        String str,
        int lastIndex,
        int length
    ) {
        int from = lastIndex > length ? lastIndex - length : 0;
        return str.substring(from, lastIndex);
    }

    public static boolean endsWith(
        String str,
        int endIndex,
        String endStr
    ) {
        int fromIndex = endIndex - endStr.length();
        if (fromIndex < 0) {
            return false;
        }
        for (int i = fromIndex, k = 0; i < endIndex; ++i, ++k) {
            if (str.charAt(i) != endStr.charAt(k)) {
                return false;
            }
        }
        return true;
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

    public static String emptyIfNull(String str) {
        return str != null ? str : EMPTY_STRING;
    }

    public static String emptyIfBlank(String str) {
        return isBlank(str) ? EMPTY_STRING : str;
    }

    public static String toLowerDashCaseWithoutSpecialCharacters(
        String str
    ) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '-' || ch == ' ' || ch == '\t' || ch == '\n') {
                builder.append('-');
            } else if (SPECIAL_CHARACTERS.indexOf(ch) < 0) {
                builder.append(Character.toLowerCase(ch));
            }
        }
        return builder.toString();
    }

    public static BigInteger toBigIntegerOrZero(String str) {
        try {
            return new BigInteger(str);
        } catch (Exception e) {
            return BigInteger.ZERO;
        }
    }

    public static String firstNotBlankValue(String... strs) {
        for (String str : strs) {
            if (isNotBlank(str)) {
                return str;
            }
        }
        return null;
    }

    @SuppressWarnings("MethodLength")
    public static String fromTemplateAndParameters(
        String template,
        Map<String, Object> parameters
    ) {
        StringBuilder builder = new StringBuilder();
        int length = template.length();
        for (int i = 0; i < length;) {
            char ch = template.charAt(i);
            char nextCh = i < length - 1 ? template.charAt(i + 1) : 0;
            if ((ch == '$' || ch == '{') && nextCh == '{') {
                String varName = null;
                StringBuilder varNameBuilder = new StringBuilder();
                for (int k = i + 2;; ++k) {
                    if (k > length - 1) {
                        builder.append(ch).append(nextCh);
                        i = k;
                        break;
                    }
                    char chK = template.charAt(k);
                    char nextChK = k < length - 1 ? template.charAt(k + 1) : 0;
                    if (ch == '$' && chK == '}') {
                        varName = varNameBuilder.toString();
                        i = k + 1;
                        break;
                    } else if (ch == '{' && chK == '}' && nextChK == '}') {
                        i = k + 2;
                        varName = varNameBuilder.toString();
                        break;
                    } else {
                        varNameBuilder.append(chK);
                    }
                }
                if (varName == null) {
                    builder.append(varNameBuilder);
                } else {
                    String value = setDateTimeToVariableIfNeed(
                        varName,
                        parameters
                    );
                    if (value.isEmpty()) {
                        if (builder.length() > 0) {
                            ch = builder.charAt(builder.length() - 1);
                            if (ch == ' ' || ch == '\t') {
                                for (; i < length; ++i) {
                                    ch = template.charAt(i);
                                    if (ch != ' ' && ch != '\t') {
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        builder.append(value);
                    }
                }
            } else if (ch == ' ' || ch == '\t') {
                char store = ch;
                for (++i; i < length; ++i) {
                    ch = template.charAt(i);
                    if (ch != ' ' && ch != '\t') {
                        builder.append(store);
                        break;
                    }
                }
            } else {
                builder.append(ch);
                ++i;
            }
        }
        for (int i = 0; i < builder.length(); ++i) {
            char ch = builder.charAt(i);
            if (ch != ' ' && ch != '\t') {
                if (i > 0) {
                    builder.delete(0, i);
                }
                break;
            }
        }
        int builderLength = builder.length();
        for (int i = builderLength - 1; i >= 0; --i) {
            char ch = builder.charAt(i);
            if (ch != ' ' && ch != '\t') {
                if (i < builderLength - 1) {
                    builder.delete(i + 1, builderLength);
                }
                break;
            } else if (i == 0) {
                builder.delete(0, builderLength);
            }
        }
        return builder.toString();
    }

    public static String toDateOrTimeOrDateTimeString(
        Object value,
        String format
    ) {
        if (isBlank(format)) {
            return value.toString();
        }
        if (value instanceof LocalDate) {
            return EzyDates.format((LocalDate) value, format);
        } else if (value instanceof LocalTime) {
            return EzyDates.format((LocalTime) value, format);
        } else if (value instanceof LocalDateTime) {
            return EzyDates.format((LocalDateTime) value, format);
        } else if (value instanceof Long) {
            return EzyDates.format((Long) value, format);
        } else if (value instanceof Instant) {
            return EzyDates.format(((Instant) value).toEpochMilli(), format);
        }
        return value.toString();
    }

    /**
     * Set date or time or datetime to a variable if need.
     * Example: The parameter value is Hello then return Hello.
     * The parameter value is time||Date||YYYY-MM-DD and parameters contains
     * {time: 2025-01-01} then return 2025-01-01.
     * The parameter value is time||Time||HH:mm:ss and parameters contains
     * {time: 20:00:00} then return 20:00:00.
     * The parameter value is time||Date||YYYY-MM-DD HH:mm:ss and parameters contains
     * {time: 2025-01-01 20:00:00} then return 2025-01-01 20:00:00.
     *
     * @param variableName the variable name.
     * @param parameters the parameters provide value for variable in the parameter value.
     * @return the parameter has set date or time or datetime if need.
     */
    public static String setDateTimeToVariableIfNeed(
        String variableName,
        Map<String, Object> parameters
    ) {
        if (variableName == null || parameters == null) {
            return EMPTY_STRING;
        }
        String[] strs = variableName.split("\\|\\|");
        String actualVariableName = strs[0];
        Object value = parameters.getOrDefault(
            actualVariableName,
            EMPTY_STRING
        );
        String type = strs.length > 1 ? strs[1] : null;
        String format = strs.length > 2 ? strs[2] : null;
        String answer;
        if (PARAMETER_TYPE_NAME_DATE.equalsIgnoreCase(type)
            || PARAMETER_TYPE_NAME_TIME.equalsIgnoreCase(type)
            || PARAMETER_TYPE_NAME_DATETIME.equalsIgnoreCase(type)
        ) {
            answer = toDateOrTimeOrDateTimeString(
                value,
                format
            );
        } else {
            answer = from(value).trim();
        }
        return answer;
    }

    public static int indexOfTextInStringIgnoreCase(
        String message,
        String keyword
    ) {
        return indexOfTextInStringIgnoreCase(message, keyword, 0);
    }

    public static int indexOfTextInStringIgnoreCase(
        String message,
        String keyword,
        int startIndex
    ) {
        int index = message.indexOf(
            keyword,
            startIndex
        );
        if (index < 0) {
            index = message.indexOf(
                keyword.toUpperCase(),
                startIndex
            );
        }
        return index;
    }
}
