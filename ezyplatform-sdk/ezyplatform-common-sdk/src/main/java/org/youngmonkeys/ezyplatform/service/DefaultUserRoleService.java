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

import com.tvd12.ezyfox.io.EzyCollections;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.entity.UserRoleId;
import org.youngmonkeys.ezyplatform.entity.UserRoleName;
import org.youngmonkeys.ezyplatform.repo.UserRoleNameRepository;
import org.youngmonkeys.ezyplatform.repo.UserRoleRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzySets.newHashSet;

@AllArgsConstructor
public class DefaultUserRoleService implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleNameRepository userRoleNameRepository;

    @Override
    public long getRoleIdByName(String roleName) {
        UserRoleName userRoleName = userRoleNameRepository
            .findByField(
                "name",
                roleName
            );
        return userRoleName == null
            ? 0L
            : userRoleName.getId();
    }

    @Override
    public Set<Long> getRoleIdsByNames(
        Collection<String> roleNames
    ) {
        if (EzyCollections.isEmpty(roleNames)) {
            return Collections.emptySet();
        }
        return userRoleNameRepository
            .findByNameIn(roleNames)
            .stream()
            .map(UserRoleName::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Long> getRoleIdMapByNames(
        Collection<String> roleNames
    ) {
        if (EzyCollections.isEmpty(roleNames)) {
            return Collections.emptyMap();
        }
        return userRoleNameRepository
            .findByNameIn(roleNames)
            .stream()
            .collect(
                Collectors.toMap(
                    UserRoleName::getName,
                    UserRoleName::getId
                )
            );
    }

    @Override
    public Set<Long> getRoleIdsByUserId(long userId) {
        return newHashSet(
            userRoleRepository.findListByField(
                "userId",
                userId
            ),
            UserRole::getRoleId
        );
    }

    @Override
    public Set<Long> getUserIdsByRoleId(long roleId, int limit) {
        return newHashSet(
            userRoleRepository.findListByField(
                "roleId",
                roleId,
                0,
                limit
            ),
            UserRole::getUserId
        );
    }

    @Override
    public Set<Long> getUserIdsByRoleName(
        String roleName,
        int limit
    ) {
        long roleId = getRoleIdByName(roleName);
        return roleId > 0
            ? getUserIdsByRoleId(roleId, limit)
            : Collections.emptySet();
    }

    @Override
    public boolean containsUserRole(long userId, long roleId) {
        return userRoleRepository.containsById(
            new UserRoleId(roleId, userId)
        );
    }

    @Override
    public boolean containsUserRole(long userId, String roleName) {
        UserRoleName userRoleName = userRoleNameRepository
            .findByField(
                "name",
                roleName
            );
        return userRoleName != null
            && containsUserRole(userId, userRoleName.getId());
    }
}
