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

package org.youngmonkeys.ezyplatform.fetcher;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;
import org.youngmonkeys.ezyplatform.repo.AdminRoleNameRepository;

import java.util.Collection;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_ADMIN_ROLE_NAME;
import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

@AllArgsConstructor
public class CommonAdminRoleEntityFetcher
    implements CommonEntityFetcher {

    private final AdminRoleNameRepository adminRoleNameRepository;

    @Override
    public CommonEntityModel getEntityById(long entityId) {
        AdminRoleName role = adminRoleNameRepository.findById(entityId);
        return role != null
            ? toCommonEntityModel(role)
            : defaultEntity(entityId, getEntityType());
    }

    @Override
    public Map<Long, CommonEntityModel> getEntityMapByIds(
        Collection<Long> entityIds
    ) {
        return newHashMapNewValues(
            newHashMap(
                adminRoleNameRepository.findListByIds(entityIds),
                AdminRoleName::getId
            ),
            this::toCommonEntityModel
        );
    }

    protected CommonEntityModel toCommonEntityModel(
        AdminRoleName entity
    ) {
        return CommonEntityModel.builder()
            .id(entity.getId())
            .code(entity.getName())
            .displayName(entity.getDisplayName())
            .url("/admins/roles/" + entity.getName())
            .icon("fas fa-users-cog")
            .build();
    }

    @Override
    public String getEntityType() {
        return TABLE_NAME_ADMIN_ROLE_NAME;
    }
}
