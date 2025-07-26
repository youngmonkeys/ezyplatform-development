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
import org.youngmonkeys.ezyplatform.entity.DataMeta;
import org.youngmonkeys.ezyplatform.model.DataMetaModel;
import org.youngmonkeys.ezyplatform.pagination.DataMetaFilter;
import org.youngmonkeys.ezyplatform.pagination.DataMetaPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataMetaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescDataMetaPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataMetaRepository;

public class PaginationDataMetaService extends CommonPaginationService<
    DataMetaModel,
    DataMetaFilter,
    DataMetaPaginationParameter,
    Long,
    DataMeta> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationDataMetaService(
        PaginationDataMetaRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        DataMetaPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected DataMetaModel convertEntity(DataMeta entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataMetaPaginationParameter defaultPaginationParameter() {
        return new IdDescDataMetaPaginationParameter();
    }
}
