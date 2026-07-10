/*
 * Copyright 2026 youngmonkeys.org
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ACCESS_TOKEN;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ADMIN_ACCESS_TOKEN;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;

public final class HttpResponses {

    private HttpResponses() {}

    public static void clearAdminAccessToken(
        HttpServletResponse response
    ) {
        Cookie tokenCookie = new Cookie(
            COOKIE_NAME_ADMIN_ACCESS_TOKEN,
            EMPTY_STRING
        );
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(ZERO);
        tokenCookie.setHttpOnly(Boolean.TRUE);
        Cookie tokenCookieExpiredAt = new Cookie(
            COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT,
            "0"
        );
        tokenCookieExpiredAt.setMaxAge(ZERO);
        tokenCookieExpiredAt.setPath("/");
        tokenCookieExpiredAt.setHttpOnly(Boolean.TRUE);
        response.addCookie(tokenCookie);
        response.addCookie(tokenCookieExpiredAt);
    }

    public static void clearUserAccessToken(
        HttpServletResponse response
    ) {
        Cookie tokenCookie = new Cookie(
            COOKIE_NAME_ACCESS_TOKEN,
            EMPTY_STRING
        );
        tokenCookie.setMaxAge(ZERO);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(Boolean.TRUE);
        response.addCookie(tokenCookie);
        Cookie tokenCookieExpiredAt = new Cookie(
            COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT,
            "0"
        );
        tokenCookieExpiredAt.setMaxAge(ZERO);
        tokenCookieExpiredAt.setPath("/");
        tokenCookieExpiredAt.setHttpOnly(Boolean.TRUE);
        response.addCookie(tokenCookieExpiredAt);
    }
}
