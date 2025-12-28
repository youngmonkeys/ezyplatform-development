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
import org.youngmonkeys.ezyplatform.entity.DataIndex;
import org.youngmonkeys.ezyplatform.model.DataIndexModel;
import org.youngmonkeys.ezyplatform.pagination.DataIndexFilter;
import org.youngmonkeys.ezyplatform.pagination.DataIndexPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataIndexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PriorityDescIdDescDataIndexPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataIndexRepository;

public class PaginationDataIndexService extends CommonPaginationService<
    DataIndexModel,
    DataIndexFilter,
    DataIndexPaginationParameter,
    Long,
    DataIndex> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationDataIndexService(
        PaginationDataIndexRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        DataIndexPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected DataIndexModel convertEntity(DataIndex entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataIndexPaginationParameter defaultPaginationParameter() {
        return new PriorityDescIdDescDataIndexPaginationParameter();
    }
}
