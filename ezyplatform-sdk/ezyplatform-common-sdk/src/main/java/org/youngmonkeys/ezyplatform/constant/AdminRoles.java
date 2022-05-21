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

package org.youngmonkeys.ezyplatform.constant;

import com.tvd12.ezyfox.collect.Sets;

import java.util.Collections;
import java.util.Set;

public final class AdminRoles {

    public static final String ROLE_NAME_ADMIN = "admin";
    public static final String ROLE_NAME_DEVOPS = "devops";
    public static final String ROLE_NAME_SUPPER_ADMIN = "supper_admin";

    public static final String ROLE_DISPLAY_SUPPER_ADMIN = "Administrator";
    public static final String ROLE_DISPLAY_DEVOPS = "DevOps";
    public static final String ROLE_DISPLAY_NAME_SUPPER_ADMIN = "Super Administrator";

    public static final Set<String> UNMODIFIABLE_ROLE_NAMES =
        Collections.unmodifiableSet(
            Sets.newHashSet(
                ROLE_NAME_SUPPER_ADMIN,
                ROLE_NAME_DEVOPS
            )
        );

    private AdminRoles() {}
}
