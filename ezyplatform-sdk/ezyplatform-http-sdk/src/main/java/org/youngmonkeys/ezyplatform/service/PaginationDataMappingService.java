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
import org.youngmonkeys.ezyplatform.entity.DataMapping;
import org.youngmonkeys.ezyplatform.entity.DataMappingId;
import org.youngmonkeys.ezyplatform.model.DataMappingModel;
import org.youngmonkeys.ezyplatform.pagination.DataMappingFilter;
import org.youngmonkeys.ezyplatform.pagination.DataMappingPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataMappingPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.MappedAtDescToDataIdDescDataMappingPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataMappingRepository;

public class PaginationDataMappingService extends CommonPaginationService<
    DataMappingModel,
    DataMappingFilter,
    DataMappingPaginationParameter,
    DataMappingId,
    DataMapping> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationDataMappingService(
        PaginationDataMappingRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        DataMappingPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected DataMappingModel convertEntity(DataMapping user) {
        return entityToModelConverter.toModel(user);
    }

    @Override
    protected DataMappingPaginationParameter defaultPaginationParameter() {
        return new MappedAtDescToDataIdDescDataMappingPaginationParameter();
    }
}
