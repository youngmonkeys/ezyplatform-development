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
        clearCookie(
            response,
            COOKIE_NAME_ADMIN_ACCESS_TOKEN,
            Boolean.TRUE
        );
        clearCookie(
            response,
            COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT,
            Boolean.TRUE
        );
    }

    public static void clearUserAccessToken(
        HttpServletResponse response
    ) {
        clearCookie(
            response,
            COOKIE_NAME_ACCESS_TOKEN,
            Boolean.TRUE
        );
        clearCookie(
            response,
            COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT,
            Boolean.TRUE
        );
    }

    public static void clearCookie(
        HttpServletResponse response,
        String cookieName,
        boolean httpOnly
    ) {
        Cookie tokenCookie = new Cookie(
            cookieName,
            EMPTY_STRING
        );
        tokenCookie.setMaxAge(ZERO);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(httpOnly);
        response.addCookie(tokenCookie);
    }
}
