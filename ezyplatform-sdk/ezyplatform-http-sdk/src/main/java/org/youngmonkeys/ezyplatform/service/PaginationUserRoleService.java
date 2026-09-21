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
import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.entity.UserRoleId;
import org.youngmonkeys.ezyplatform.model.UserRoleModel;
import org.youngmonkeys.ezyplatform.pagination.UserRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRolePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserRolePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.RoleIdDescUserIdDescUserRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserRoleRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationUserRoleService extends CommonPaginationService<
    UserRoleModel,
    UserRoleFilter,
    UserRolePaginationParameter,
    UserRoleId,
    UserRole> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserRoleService(
        PaginationUserRoleRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserRolePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected UserRoleModel convertEntity(UserRole entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserRolePaginationParameter defaultPaginationParameter() {
        return new RoleIdDescUserIdDescUserRolePaginationParameter();
    }
}
