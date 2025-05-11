/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.validator;

import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.service.UserService;

import java.util.Collections;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static java.util.Collections.singletonMap;

@AllArgsConstructor
public class UserValidator {

    private final UserService userService;

    public UserModel validateUserId(long userId) {
        UserModel user = userService.getUserById(userId);
        if (user == null) {
            throw new HttpNotFoundException(
                singletonMap("user", "notFound")
            );
        }
        return user;
    }

    public UserModel validateUserUuid(String uuid) {
        UserModel user = null;
        if (isNotBlank(uuid)) {
            user = userService.getUserByUuid(uuid);
        }
        if (user == null) {
            throw new HttpNotFoundException(
                Collections.singletonMap("user", "notFound")
            );
        }
        return user;
    }

    public UserModel validateUsername(String username) {
        UserModel user = null;
        if (isNotBlank(username)) {
            user = userService.getUserByUsername(username);
        }
        if (user == null) {
            throw new HttpNotFoundException(
                Collections.singletonMap("user", "notFound")
            );
        }
        return user;
    }
}
