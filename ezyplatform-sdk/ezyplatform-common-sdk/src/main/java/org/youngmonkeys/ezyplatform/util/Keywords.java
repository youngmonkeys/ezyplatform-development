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

import java.util.*;

import static com.tvd12.ezyfox.io.EzyStrings.*;

public final class Keywords {

    public static final int DEFAULT_MAX_KEYWORD_LENGTH = 300;

    private Keywords() {}

    public static Set<String> toKeywords(String str) {
        return toKeywords(str, false);
    }

    public static Set<String> toKeywords(
        String str,
        boolean nullIfBlank
    ) {
        return toKeywords(
            str,
            nullIfBlank,
            DEFAULT_MAX_KEYWORD_LENGTH
        );
    }

    public static Set<String> toKeywords(
        String str,
        boolean nullIfBlank,
        int maxKeywordLength
    ) {
        if (isBlank(str)) {
            return nullIfBlank ? null : Collections.emptySet();
        }
        Set<String> answer = new HashSet<>();
        List<String> words = splitString(str);
        int longKeywordLength = 0;
        Queue<String> longKeyword = null;
        for (String word : words) {
            String wordTrim = word.trim();
            if (wordTrim.length() > maxKeywordLength) {
                wordTrim = wordTrim.substring(0, maxKeywordLength);
            }
            if (longKeyword == null) {
                longKeyword = new LinkedList<>();
            }
            longKeyword.add(wordTrim);
            longKeywordLength += wordTrim.length();
            if (longKeyword.size() > 1) {
                longKeywordLength += 1;
            }
            while (longKeywordLength > maxKeywordLength) {
                String firstWord = longKeyword.poll();
                longKeywordLength -= Objects.requireNonNull(firstWord).length();
                longKeywordLength -= 1;
            }
            answer.add(wordTrim);
            answer.add(wordTrim.toLowerCase());
            String longKeywordString = String.join(" ", longKeyword);
            answer.add(longKeywordString);
            answer.add(longKeywordString.toLowerCase());
        }
        return answer;
    }

    public static String keywordFromEmail(String email) {
        int atIndex = email.indexOf("@");
        return atIndex < 1
            ? EMPTY_STRING
            : email.substring(0, atIndex).toLowerCase();
    }

    public static List<String> splitString(String str) {
        List<String> answer = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (isWordSeparator(ch)) {
                if (buffer.length() == 0) {
                    continue;
                }
                answer.add(buffer.toString());
                buffer.delete(0, buffer.length());
            } else {
                buffer.append(ch);
            }
        }
        if (buffer.length() > 0) {
            answer.add(buffer.toString());
        }
        return answer;
    }
}
