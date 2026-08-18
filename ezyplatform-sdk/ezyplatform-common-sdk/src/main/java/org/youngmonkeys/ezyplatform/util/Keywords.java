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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static java.lang.Character.isWhitespace;
import static org.youngmonkeys.ezyplatform.util.Strings.substring;

public final class Keywords {

    public static final int DEFAULT_MAX_KEYWORD_LENGTH = 30;

    private Keywords() {}

    /**
     * Equivalent to {@link #toKeywords(String, boolean)} with
     * {@code nullIfBlank} set to {@code false}.
     *
     * @param str the string to extract keywords from
     * @return the extracted keywords, or an empty list if {@code str}
     *         is blank
     */
    public static List<String> toKeywords(
        String str
    ) {
        return toKeywords(str, Boolean.FALSE);
    }

    /**
     * Equivalent to {@link #toKeywords(String, boolean, int)} with
     * {@code maxKeywordLength} set to {@link #DEFAULT_MAX_KEYWORD_LENGTH}.
     *
     * @param str the string to extract keywords from
     * @param nullIfBlank whether to return {@code null} instead of an
     *                     empty list when {@code str} is blank
     * @return the extracted keywords, or {@code null}/empty list (per
     *         {@code nullIfBlank}) if {@code str} is blank
     */
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

    /**
     * Builds prefix keywords for substring search: for every
     * non-whitespace character in {@code str}, takes the substring
     * starting at that character and up to {@code maxKeywordLength}
     * characters long, trims it and lower-cases it. This yields one
     * keyword per non-whitespace position, so a later prefix match
     * against these keywords can find {@code str} regardless of which
     * character the match starts at.
     *
     * @param str the string to extract keywords from
     * @param nullIfBlank whether to return {@code null} instead of an
     *                     empty list when {@code str} is blank
     * @param maxKeywordLength the maximum length of each keyword; if
     *                          {@code 0}, an empty list is returned
     * @return the extracted keywords, or {@code null}/empty list (per
     *         {@code nullIfBlank}) if {@code str} is blank, or an empty
     *         list if {@code maxKeywordLength} is {@code 0}
     */
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

    /**
     * Extracts search keywords from a free-text query: trims and
     * lower-cases {@code query}, splits it by {@code splitPattern},
     * drops tokens shorter than {@code minKeywordLength} or contained in
     * {@code stopWords}, removes duplicates, sorts the remaining tokens
     * longest-first (most specific first), and caps the result at
     * {@code maxKeywords} tokens.
     *
     * @param query the free-text query to extract keywords from
     * @param splitPattern the regex used to split {@code query} into
     *                       tokens
     * @param minKeywordLength the minimum token length to keep as a
     *                          keyword
     * @param stopWords tokens to discard even if they meet
     *                   {@code minKeywordLength}
     * @param maxKeywords the maximum number of keywords to return
     * @return the extracted keywords, longest first, capped at
     *         {@code maxKeywords}
     */
    public static List<String> toKeywords(
        String query,
        String splitPattern,
        int minKeywordLength,
        Set<String> stopWords,
        int maxKeywords
    ) {
        return Arrays
            .stream(
                query
                    .trim()
                    .toLowerCase()
                    .split(splitPattern)
            )
            .filter(token -> token.length() >= minKeywordLength)
            .filter(token -> !stopWords.contains(token))
            .distinct()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .limit(maxKeywords)
            .collect(Collectors.toList());
    }

    /**
     * Builds search keywords for an email address: the email itself
     * (capped at {@link #DEFAULT_MAX_KEYWORD_LENGTH} characters), plus
     * the prefix keywords (see {@link #toKeywords(String)}) of its
     * local part, i.e. the part before {@code @} (see
     * {@link #keywordFromEmail(String)}).
     *
     * @param email the email address to extract keywords from
     * @return the extracted keywords, or an empty list if {@code email}
     *         is blank
     */
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

    /**
     * Extracts the local part of an email address, e.g. {@code "john"}
     * from {@code "john@example.com"}.
     *
     * @param email the email address to extract from
     * @return the lower-cased local part of {@code email}, or an empty
     *         string if {@code email} has no {@code @} or starts with
     *         one
     */
    public static String keywordFromEmail(String email) {
        int atIndex = email.indexOf("@");
        return atIndex < 1
            ? EMPTY_STRING
            : email.substring(0, atIndex).toLowerCase();
    }
}
