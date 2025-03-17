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
import org.youngmonkeys.ezyplatform.entity.UniqueData;
import org.youngmonkeys.ezyplatform.entity.UniqueDataId;
import org.youngmonkeys.ezyplatform.model.UniqueDataModel;
import org.youngmonkeys.ezyplatform.pagination.NumberValueDescDataIdDescUniqueDataPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UniqueDataFilter;
import org.youngmonkeys.ezyplatform.pagination.UniqueDataPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UniqueDataPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationUniqueDataRepository;

public class PaginationUniqueDataService extends CommonPaginationService<
    UniqueDataModel,
    UniqueDataFilter,
    UniqueDataPaginationParameter,
    UniqueDataId,
    UniqueData> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUniqueDataService(
        PaginationUniqueDataRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UniqueDataPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected UniqueDataModel convertEntity(UniqueData user) {
        return entityToModelConverter.toModel(user);
    }

    @Override
    protected UniqueDataPaginationParameter defaultPaginationParameter() {
        return new NumberValueDescDataIdDescUniqueDataPaginationParameter();
    }
}
