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
import org.youngmonkeys.ezyplatform.entity.UserRoleName;
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;
import org.youngmonkeys.ezyplatform.repo.UserRoleNameRepository;

import java.util.Collection;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_USER_ROLE_NAME;
import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

@AllArgsConstructor
public class CommonUserRoleEntityFetcher
    implements CommonEntityFetcher {

    private final UserRoleNameRepository userRoleNameRepository;

    @Override
    public CommonEntityModel getEntityById(long entityId) {
        UserRoleName role = userRoleNameRepository.findById(entityId);
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
                userRoleNameRepository.findListByIds(entityIds),
                UserRoleName::getId
            ),
            this::toCommonEntityModel
        );
    }

    protected CommonEntityModel toCommonEntityModel(
        UserRoleName entity
    ) {
        return CommonEntityModel.builder()
            .id(entity.getId())
            .code(entity.getName())
            .displayName(entity.getDisplayName())
            .url("/users/roles/" + entity.getName())
            .icon("fas fa-user-cog")
            .build();
    }

    @Override
    public String getEntityType() {
        return TABLE_NAME_USER_ROLE_NAME;
    }
}
