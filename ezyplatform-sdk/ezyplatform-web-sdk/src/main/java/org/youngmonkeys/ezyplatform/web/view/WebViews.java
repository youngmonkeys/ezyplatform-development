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

import com.tvd12.ezyhttp.server.core.view.Redirect;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.util.HttpRequests.addLanguageToUri;

public final class WebViews {

    private WebViews() {}

    public static Redirect redirectToLogin(
        HttpServletRequest request
    ) {
        Cookie tokenCookie = new Cookie(
            COOKIE_NAME_ACCESS_TOKEN,
            ""
        );
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        Cookie tokenCookieExpiredAt = new Cookie(
            COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT,
            "0"
        );
        tokenCookieExpiredAt.setMaxAge(0);
        tokenCookieExpiredAt.setPath("/");
        return Redirect.builder()
            .uri(addLanguageToUri(request, "/login"))
            .addCookie(tokenCookie)
            .addCookie(tokenCookieExpiredAt)
            .build();
    }
}
