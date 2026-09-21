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
import org.youngmonkeys.ezyplatform.entity.AdminRole;
import org.youngmonkeys.ezyplatform.entity.AdminRoleId;
import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.RoleIdDescAdminIdDescAdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRoleRepository;

public class PaginationAdminRoleService extends CommonPaginationService<
    AdminRoleModel,
    AdminRoleFilter,
    AdminRolePaginationParameter,
    AdminRoleId,
    AdminRole> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminRoleService(
        PaginationAdminRoleRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminRolePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected AdminRoleModel convertEntity(AdminRole entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected AdminRolePaginationParameter defaultPaginationParameter() {
        return new RoleIdDescAdminIdDescAdminRolePaginationParameter();
    }
}
