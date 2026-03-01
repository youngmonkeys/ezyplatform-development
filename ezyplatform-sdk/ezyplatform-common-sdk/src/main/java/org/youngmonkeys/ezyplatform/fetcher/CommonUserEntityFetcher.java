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
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.service.UserService;

import java.util.Collection;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_USER;
import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

@AllArgsConstructor
public class CommonUserEntityFetcher
    implements CommonEntityFetcher {

    private final UserService userService;

    @Override
    public CommonEntityModel getEntityById(long entityId) {
        UserNameModel model = userService.getUsernameById(
            entityId
        );
        return model != null
            ? toCommonEntityModel(model)
            : defaultEntity(entityId, getEntityType());
    }

    @Override
    public Map<Long, CommonEntityModel> getEntityMapByIds(
        Collection<Long> entityIds
    ) {
        return newHashMapNewValues(
            userService.getUsernameMapByIds(entityIds),
            this::toCommonEntityModel
        );
    }

    protected CommonEntityModel toCommonEntityModel(
        UserNameModel model
    ) {
        return CommonEntityModel.builder()
            .id(model.getUserId())
            .code(model.getUsername())
            .displayName(model.getName())
            .url(getUriPrefix() + "/" + model.getUsername())
            .build();
    }

    protected String getUriPrefix() {
        return "/users";
    }

    @Override
    public String getEntityType() {
        return TABLE_NAME_USER;
    }
}
