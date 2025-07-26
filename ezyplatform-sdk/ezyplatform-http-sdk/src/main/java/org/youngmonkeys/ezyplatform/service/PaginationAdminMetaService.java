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
import org.youngmonkeys.ezyplatform.entity.AdminMeta;
import org.youngmonkeys.ezyplatform.model.AdminMetaModel;
import org.youngmonkeys.ezyplatform.pagination.AdminMetaFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminMetaPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminMetaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescAdminMetaPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminMetaRepository;

public class PaginationAdminMetaService extends CommonPaginationService<
    AdminMetaModel,
    AdminMetaFilter,
    AdminMetaPaginationParameter,
    Long,
    AdminMeta> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminMetaService(
        PaginationAdminMetaRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminMetaPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected AdminMetaModel convertEntity(AdminMeta entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected AdminMetaPaginationParameter defaultPaginationParameter() {
        return new IdDescAdminMetaPaginationParameter();
    }
}
