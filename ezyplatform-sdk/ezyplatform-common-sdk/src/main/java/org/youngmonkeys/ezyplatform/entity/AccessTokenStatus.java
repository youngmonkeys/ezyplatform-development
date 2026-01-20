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

package org.youngmonkeys.ezyplatform.entity;

import com.tvd12.ezyfox.util.EzyEnums;

import java.util.Map;

public enum AccessTokenStatus {
    ACTIVATED,
    ACTIVATED_2FA,
    INACTIVATED,
    EXPIRED,
    REVOKED,
    INVALID,
    PENDING,
    BLACKLISTED,
    REFRESHING,
    BLOCKED,
    DELETED,
    WAITING_2FA;

    private static final Map<String, AccessTokenStatus> MAP_BY_NAME =
        EzyEnums.enumMap(AccessTokenStatus.class, AccessTokenStatus::toString);

    public static AccessTokenStatus of(String value) {
        return of(value, INACTIVATED);
    }

    public static AccessTokenStatus of(
        String value,
        AccessTokenStatus defaultStatus
    ) {
        return MAP_BY_NAME.getOrDefault(value, defaultStatus);
    }

    public boolean equalsValue(String value) {
        return value != null && this.toString().equals(value);
    }
}
