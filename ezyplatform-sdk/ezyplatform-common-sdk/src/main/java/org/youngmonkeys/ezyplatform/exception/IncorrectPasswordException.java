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

package org.youngmonkeys.ezyplatform.exception;

import static java.util.Collections.singletonMap;
import static org.youngmonkeys.ezyplatform.util.Strings.hideSensitiveInformation;

public class IncorrectPasswordException extends BadRequestException {

    public IncorrectPasswordException() {
        this("password");
    }

    public IncorrectPasswordException(String passwordField) {
        super(singletonMap(passwordField, "incorrect"));
    }

    public IncorrectPasswordException(String passwordField, String msg) {
        super(singletonMap(passwordField, "incorrect"), msg);
    }

    public static IncorrectPasswordException ofUserId(long userId) {
        return new IncorrectPasswordException(
            "password",
            "Incorrect password of user: " + userId
        );
    }

    public static IncorrectPasswordException ofUserName(String username) {
        return new IncorrectPasswordException(
            "password",
            "Incorrect password of user: " +
                hideSensitiveInformation(username, 2, 6)
        );
    }

    public static IncorrectPasswordException ofAdminId(long adminId) {
        return new IncorrectPasswordException(
            "password",
            "Incorrect password of admin: " + adminId
        );
    }
}
