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

public final class Htmls {

    public static final String TAG_NAME_SCRIPT = "script";

    private Htmls() {}

    public static boolean containsScriptTag(String content) {
        int contentLength = content.length();
        for (int i = 0; i < contentLength; ++i) {
            char ch = content.charAt(i);
            if (ch == '<') {
                int k = 0;
                for (++i; i < contentLength; ++i) {
                    ch = content.charAt(i);
                    if (ch != '<') {
                        break;
                    }
                }
                for (; i < contentLength; ++i) {
                    ch = content.charAt(i);
                    if (ch != ' ' && ch != '\t' && ch != '\n') {
                        break;
                    }
                }
                for (; i < contentLength; ++i) {
                    ch = content.charAt(i);
                    if (ch != TAG_NAME_SCRIPT.charAt(k++)) {
                        return false;
                    }
                    if (k == TAG_NAME_SCRIPT.length()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("MethodLength")
    public static String escapeScriptTag(String content) {
        StringBuilder answer = new StringBuilder();
        int contentLength = content.length();
        for (int i = 0; i < contentLength; ++i) {
            char ch = content.charAt(i);
            if (ch != '<') {
                answer.append(ch);
                continue;
            }

            // for case <<<script
            for (++i; i < contentLength; ++i) {
                ch = content.charAt(i);
                if (ch != '<') {
                    break;
                } else {
                    answer.append(ch);
                }
            }

            // for case <   script
            StringBuilder before = new StringBuilder();
            for (; i < contentLength; ++i) {
                ch = content.charAt(i);
                if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '/') {
                    break;
                } else {
                    before.append(ch);
                }
            }
            StringBuilder tagNameBuilder = new StringBuilder();
            for (; i < contentLength; ++i) {
                ch = content.charAt(i);
                if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '>') {
                    tagNameBuilder.append(ch);
                } else {
                    --i;
                    break;
                }
            }
            String tagName = tagNameBuilder.toString();
            if (tagName.equals(TAG_NAME_SCRIPT)) {
                answer
                    .append("&lt")
                    .append(before)
                    .append(tagName);

                // for case <script   >
                for (++i; i < contentLength; ++i) {
                    ch = content.charAt(i);
                    if (ch == '>') {
                        answer.append("&gt");
                        break;
                    } else {
                        answer.append(ch);
                    }
                }
            } else {
                answer
                    .append("<")
                    .append(before)
                    .append(tagName);
            }
        }
        return answer.toString();
    }
}
