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
import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.model.AdminRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNamePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PriorityAscIdAscAdminRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRoleNameRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationAdminRoleNameService extends CommonPaginationService<
    AdminRoleNameModel,
    AdminRoleNameFilter,
    AdminRoleNamePaginationParameter,
    Long,
    AdminRoleName> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminRoleNameService(
        PaginationAdminRoleNameRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminRoleNamePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected AdminRoleNameModel convertEntity(AdminRoleName entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected AdminRoleNamePaginationParameter defaultPaginationParameter() {
        return new PriorityAscIdAscAdminRoleNamePaginationParameter();
    }
}
