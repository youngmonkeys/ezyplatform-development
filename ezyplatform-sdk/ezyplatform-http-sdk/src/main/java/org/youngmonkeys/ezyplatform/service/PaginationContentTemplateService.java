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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.ContentTemplate;
import org.youngmonkeys.ezyplatform.model.ContentTemplateModel;
import org.youngmonkeys.ezyplatform.pagination.ContentTemplateFilter;
import org.youngmonkeys.ezyplatform.pagination.ContentTemplatePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.ContentTemplatePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescContentTemplatePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationContentTemplateRepository;

public class PaginationContentTemplateService extends CommonPaginationService<
    ContentTemplateModel,
    ContentTemplateFilter,
    ContentTemplatePaginationParameter,
    Long,
    ContentTemplate> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationContentTemplateService(
        PaginationContentTemplateRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        ContentTemplatePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected ContentTemplateModel convertEntity(ContentTemplate entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected ContentTemplatePaginationParameter defaultPaginationParameter() {
        return new IdDescContentTemplatePaginationParameter();
    }
}
