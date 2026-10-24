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

package org.youngmonkeys.ezyplatform.web.view;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.view.Redirect;

import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.util.HttpRequests.addLanguageToUri;
import static org.youngmonkeys.ezyplatform.util.Uris.urlEncode;

public final class WebViews {

    private WebViews() {}

    public static Redirect redirectToLogin(
        HttpServletRequest request,
        boolean addCallbackUri
    ) {
        Redirect.Builder builder = Redirect.builder()
            .uri(addLanguageToUri(request, "/login"));
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        if (addCallbackUri
            && HttpMethod.GET.equalsValue(method)
            && !requestUri.equals("/register")
            && !requestUri.equals("/login")
            && !requestUri.equals("/logout")
        ) {
            String queryString = request.getQueryString();
            String callbackUri = requestUri +
                (isBlank(queryString) ? "" : ("?" + queryString));
            builder.addAttribute("callbackUri", callbackUri);
        }
        return builder.build();
    }

    public static Redirect redirectTo2Fa(
        HttpServletRequest request
    ) {
        String uri = "/2fa";
        if (HttpMethod.GET.equalsValue(request.getMethod())) {
            String queryString = request.getQueryString();
            String callbackUrl = request.getRequestURI() +
                (isBlank(queryString) ? "" : ("?" + queryString));
            uri += "?callbackUrl=" + urlEncode(callbackUrl);
        }
        return Redirect.to(addLanguageToUri(request, uri));
    }
}
