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

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescMediaPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.MediaFilter;
import org.youngmonkeys.ezyplatform.pagination.MediaPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.MediaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationMediaRepository;

public class PaginationMediaService extends CommonPaginationService<
    MediaModel,
    MediaFilter,
    MediaPaginationParameter,
    Long,
    Media> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationMediaService(
        PaginationMediaRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        MediaPaginationParameterConverter mediaPaginationParameterConverter
    ) {
        super(repository, mediaPaginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected MediaModel convertEntity(Media media) {
        return entityToModelConverter.toModel(media);
    }

    @Override
    protected MediaPaginationParameter defaultPaginationParameter() {
        return new IdDescMediaPaginationParameter();
    }
}
