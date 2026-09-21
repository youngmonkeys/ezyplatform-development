/*
 * Copyright 2026 youngmonkeys.org
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
import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.entity.DataI18nId;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.pagination.DataI18nFilter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.DataTypeDescDataIdDescLanguageDescFieldNameDescDataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataI18nRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationDataI18nService extends CommonPaginationService<
    DataI18nModel,
    DataI18nFilter,
    DataI18nPaginationParameter,
    DataI18nId,
    DataI18n> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationDataI18nService(
        PaginationDataI18nRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        DataI18nPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected DataI18nModel convertEntity(DataI18n entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataI18nPaginationParameter defaultPaginationParameter() {
        return new DataTypeDescDataIdDescLanguageDescFieldNameDescDataI18nPaginationParameter();
    }
}
