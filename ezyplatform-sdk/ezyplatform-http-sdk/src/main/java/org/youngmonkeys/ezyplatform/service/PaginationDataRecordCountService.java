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
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.model.DataRecordCountModel;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountFilter;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescDataRecordCountPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataRecordCountRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationDataRecordCountService extends CommonPaginationService<
    DataRecordCountModel,
    DataRecordCountFilter,
    DataRecordCountPaginationParameter,
    Long,
    DataRecordCount> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationDataRecordCountService(
        PaginationDataRecordCountRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        DataRecordCountPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected DataRecordCountModel convertEntity(DataRecordCount entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataRecordCountPaginationParameter defaultPaginationParameter() {
        return new IdDescDataRecordCountPaginationParameter();
    }
}
