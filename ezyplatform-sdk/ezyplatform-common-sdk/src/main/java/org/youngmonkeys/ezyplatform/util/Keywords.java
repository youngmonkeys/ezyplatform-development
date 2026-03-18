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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static java.lang.Character.isWhitespace;
import static org.youngmonkeys.ezyplatform.util.Strings.substring;

public final class Keywords {

    public static final int DEFAULT_MAX_KEYWORD_LENGTH = 30;

    private Keywords() {}

    public static List<String> toKeywords(
        String str
    ) {
        return toKeywords(str, Boolean.FALSE);
    }

    public static List<String> toKeywords(
        String str,
        boolean nullIfBlank
    ) {
        return toKeywords(
            str,
            nullIfBlank,
            DEFAULT_MAX_KEYWORD_LENGTH
        );
    }

    @SuppressWarnings("MethodLength")
    public static List<String> toKeywords(
        String str,
        boolean nullIfBlank,
        int maxKeywordLength
    ) {
        if (isBlank(str)) {
            return nullIfBlank ? null : Collections.emptyList();
        }
        if (maxKeywordLength == 0) {
            return Collections.emptyList();
        }
        List<String> answer = new ArrayList<>();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (!isWhitespace(ch)) {
                answer.add(
                    substring(str, i, i + maxKeywordLength)
                    .trim()
                    .toLowerCase()
                );
            }
        }
        return answer;
    }

    public static List<String> keywordsFromEmail(
        String email
    ) {
        if (isBlank(email)) {
            return Collections.emptyList();
        }
        List<String> keywords = new ArrayList<>();
        String emailKeyword = email;
        int maxLength = DEFAULT_MAX_KEYWORD_LENGTH;
        if (email.length() > maxLength) {
            emailKeyword = email.substring(0, maxLength);
        }
        keywords.add(emailKeyword);
        keywords.addAll(toKeywords(keywordFromEmail(email)));
        return keywords;
    }

    public static String keywordFromEmail(String email) {
        int atIndex = email.indexOf("@");
        return atIndex < 1
            ? EMPTY_STRING
            : email.substring(0, atIndex).toLowerCase();
    }
}
