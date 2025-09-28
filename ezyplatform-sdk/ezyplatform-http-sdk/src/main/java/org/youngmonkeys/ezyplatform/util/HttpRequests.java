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

import com.tvd12.ezyhttp.server.core.util.HttpServletRequests;

import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class HttpRequests {

    private HttpRequests() {}

    public static String getLanguage(
        HttpServletRequest request
    ) {
        return HttpServletRequests.getRequestValue(
            request,
            "lang"
        );
    }

    public static String addLanguageToUri(
        HttpServletRequest request,
        String uri
    ) {
        String lang = getLanguage(request);
        if (isBlank(lang)) {
            return uri;
        }
        char concatSymbol = uri.indexOf('?') < 0
            ? '?'
            : '&';
        return uri + concatSymbol + "lang=" + lang;
    }
}
