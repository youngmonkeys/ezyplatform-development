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
import org.youngmonkeys.ezyplatform.entity.UserRoleName;
import org.youngmonkeys.ezyplatform.model.UserRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNamePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PriorityAscIdAscUserRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserRoleNameRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationUserRoleNameService extends CommonPaginationService<
    UserRoleNameModel,
    UserRoleNameFilter,
    UserRoleNamePaginationParameter,
    Long,
    UserRoleName> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserRoleNameService(
        PaginationUserRoleNameRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserRoleNamePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected UserRoleNameModel convertEntity(UserRoleName entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserRoleNamePaginationParameter defaultPaginationParameter() {
        return new PriorityAscIdAscUserRoleNamePaginationParameter();
    }
}
