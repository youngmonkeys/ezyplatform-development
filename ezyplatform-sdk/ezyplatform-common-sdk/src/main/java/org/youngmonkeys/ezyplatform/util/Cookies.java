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

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class Cookies {

    private Cookies() {}

    public static String extractCookieDomain(String host) {
        if (isBlank(host)) {
            return EMPTY_STRING;
        }
        int dotCount = 0;
        for (int i = host.length() - 1; i >= 0; --i) {
            char ch = host.charAt(i);
            if (ch == '.') {
                if ((++dotCount) == 2) {
                    return host.substring(i);
                }
            }
        }
        return host;
    }
}
