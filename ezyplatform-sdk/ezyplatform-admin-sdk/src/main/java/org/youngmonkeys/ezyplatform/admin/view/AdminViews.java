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

package org.youngmonkeys.ezyplatform.admin.view;

import com.tvd12.ezyhttp.server.core.view.Redirect;

import javax.servlet.http.Cookie;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ADMIN_ACCESS_TOKEN;

public final class AdminViews {

    private AdminViews() {}

    public static Redirect redirectToLogin() {
        Cookie tokenCookie = new Cookie(
            COOKIE_NAME_ADMIN_ACCESS_TOKEN,
            ""
        );
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        return Redirect.builder()
            .uri("/login")
            .addCookie(tokenCookie)
            .build();
    }
}
