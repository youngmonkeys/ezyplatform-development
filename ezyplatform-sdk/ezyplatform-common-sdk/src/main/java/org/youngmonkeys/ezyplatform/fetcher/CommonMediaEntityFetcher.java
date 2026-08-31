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
import org.youngmonkeys.ezyplatform.model.MediaNameModel;
import org.youngmonkeys.ezyplatform.service.MediaService;

import java.util.Collection;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_MEDIA;
import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

@AllArgsConstructor
public class CommonMediaEntityFetcher
    implements CommonEntityFetcher {

    private final MediaService mediaService;

    @Override
    public CommonEntityModel getEntityById(long entityId) {
        MediaNameModel media = mediaService.getMediaNameById(entityId);
        return media != null
            ? toCommonEntityModel(media)
            : defaultEntity(entityId, getEntityType());
    }

    @Override
    public Map<Long, CommonEntityModel> getEntityMapByIds(
        Collection<Long> entityIds
    ) {
        return newHashMapNewValues(
            mediaService.getMediaNameMapByIds(entityIds),
            this::toCommonEntityModel
        );
    }

    protected CommonEntityModel toCommonEntityModel(
        MediaNameModel model
    ) {
        return CommonEntityModel.builder()
            .id(model.getId())
            .code(model.getOriginalName())
            .displayName(
                isNotBlank(model.getTitle())
                    ? model.getTitle()
                    : model.getAlternativeText()
            )
            .url(model.getUrlOrNull())
            .icon("far fa-image")
            .build();
    }

    @Override
    public String getEntityType() {
        return TABLE_NAME_MEDIA;
    }
}
