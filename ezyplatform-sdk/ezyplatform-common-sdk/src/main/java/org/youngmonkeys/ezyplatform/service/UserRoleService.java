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

package org.youngmonkeys.ezyplatform.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.LIMIT_1500_RECORDS;

public interface UserRoleService {

    long getRoleIdByName(String roleName);

    Set<Long> getRoleIdsByNames(
        Collection<String> roleNames
    );

    Map<String, Long> getRoleIdMapByNames(
        Collection<String> roleNames
    );

    Set<Long> getRoleIdsByUserId(long userId);

    Set<Long> getUserIdsByRoleId(long roleId, int limit);

    default Set<Long> getUserIdsByRoleId(long roleId) {
        return getUserIdsByRoleId(
            roleId,
            LIMIT_1500_RECORDS
        );
    }

    Set<Long> getUserIdsByRoleName(String roleName, int limit);

    default Set<Long> getUserIdsByRoleName(String roleName) {
        return getUserIdsByRoleName(roleName, LIMIT_1500_RECORDS);
    }

    boolean containsUserRole(long userId, long roleId);

    boolean containsUserRole(long userId, String roleName);
}
