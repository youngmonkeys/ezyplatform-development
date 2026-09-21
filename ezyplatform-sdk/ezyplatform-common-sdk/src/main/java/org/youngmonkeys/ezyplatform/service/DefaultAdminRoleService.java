/*
 * Copyright 2023 youngmonkeys.org
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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AdminRole;
import org.youngmonkeys.ezyplatform.entity.AdminRoleId;
import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.model.AdminRoleNameModel;
import org.youngmonkeys.ezyplatform.repo.AdminRoleNameRepository;
import org.youngmonkeys.ezyplatform.repo.AdminRoleRepository;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxValueMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzySets.newHashSet;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@AllArgsConstructor
public class DefaultAdminRoleService implements AdminRoleService {

    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleNameRepository adminRoleNameRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    @Override
    public long getRoleIdByName(
        String roleName
    ) {
        AdminRoleName adminRoleName = adminRoleNameRepository
            .findByField(
                "name",
                roleName
            );
        return adminRoleName == null
            ? ZERO_LONG
            : adminRoleName.getId();
    }

    @Override
    public List<Long> getAdminIdsByRoleName(
        String roleName
    ) {
        return newArrayList(
            adminRoleRepository.findAdminIdsByRoleName(roleName),
            IdResult::getId
        );
    }

    @Override
    public boolean containsAdminRole(
        long roleId,
        long adminId
    ) {
        return adminRoleRepository.containsById(
            new AdminRoleId(roleId, adminId)
        );
    }

    @Override
    public boolean containsAdminRoleName(
        long roleId
    ) {
        return adminRoleNameRepository
            .containsById(roleId);
    }

    @Override
    public boolean containsAllAdminRoleIds(
        Collection<Long> roleIds
    ) {
        return getAdminRoleNamesByIds(roleIds).size()
            == roleIds.size();
    }

    @Override
    public List<AdminRoleNameModel> getAdminRoleNames() {
        return newArrayList(
            adminRoleNameRepository.findAll(),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<AdminRoleNameModel> getAdminRoleNamesByIds(
        Collection<Long> roleIds
    ) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            adminRoleNameRepository.findListByIds(roleIds),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<AdminRoleNameModel> getAdminRoleNamesByAdminId(
        long adminId
    ) {
        List<Long> roleIds = newArrayList(
            adminRoleRepository
                .findListByField("adminId", adminId),
            AdminRole::getRoleId
        );
        return getAdminRoleNamesByIds(roleIds);
    }

    @Override
    public List<AdminRoleNameModel> getAdminRoleNamesByPriorityGte(
        int priorityGte
    ) {
        return newArrayList(
            adminRoleNameRepository.findByByPriorityGteOrderByPriorityAndId(
                priorityGte
            ),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<AdminRoleNameModel> getVisibleAdminRoleNamesByAdminId(
        long adminId
    ) {
        return getAdminRoleNamesByPriorityGte(
            getMinAdminRolePriority(adminId)
        );
    }

    @Override
    public AdminRoleNameModel getAdminRoleNameById(
        long id
    ) {
        return entityToModelConverter.toModel(
            adminRoleNameRepository.findById(id)
        );
    }

    @Override
    public AdminRoleNameModel getAdminRoleNameByName(String name) {
        return entityToModelConverter.toModel(
            adminRoleNameRepository.findByField("name", name)
        );
    }

    @Override
    public AdminRoleNameModel getAdminRoleNameByNameOrDisplayName(
        String name,
        String displayName
    ) {
        RxValueMap map = Reactive.multiple()
            .register("name", () ->
                adminRoleNameRepository.findByField(
                    "name",
                    name
                )
            )
            .register("displayName", () ->
                adminRoleNameRepository.findByField(
                    "displayName",
                    displayName
                )
            )
            .blockingGet();
        AdminRoleName entity = map.firstValueOrNull();
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public Set<Long> getRoleIdsByAdminId(
        long adminId
    ) {
        return newHashSet(
            adminRoleRepository.findListByField(
                "adminId",
                adminId
            ),
            AdminRole::getRoleId
        );
    }

    @Override
    public List<AdminRoleModel> getAdminRolesByRoleId(
        long roleId
    ) {
        return newArrayList(
            adminRoleRepository
                .findListByField("roleId", roleId),
            entityToModelConverter::toModel
        );
    }

    @Override
    public int getMinAdminRolePriority(
        long adminId
    ) {
        return adminRoleNameRepository.findMinAdminRoleName(
            adminId
        )
            .map(AdminRoleName::getPriority)
            .orElse(ZERO);
    }

    @Override
    public int getMinPriorityByRoleIds(
        Collection<Long> roleIds
    ) {
        if (roleIds.isEmpty()) {
            throw new IllegalArgumentException("roleIds is required");
        }
        return adminRoleNameRepository.findMinRoleByIds(
            roleIds
        )
            .map(AdminRoleName::getPriority)
            .orElse(ZERO);
    }

    @Override
    public long countAllRoles() {
        return adminRoleNameRepository.count();
    }

    @Override
    public long countAdminVisibleRoles(
        long adminId
    ) {
        int minPriority = getMinAdminRolePriority(adminId);
        return adminRoleNameRepository.countByPriorityGte(minPriority);
    }
}
