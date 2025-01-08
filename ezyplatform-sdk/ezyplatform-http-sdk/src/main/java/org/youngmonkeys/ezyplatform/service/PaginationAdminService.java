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
import org.youngmonkeys.ezyplatform.entity.Admin;
import org.youngmonkeys.ezyplatform.model.AdminModel;
import org.youngmonkeys.ezyplatform.pagination.AdminCommonPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.AdminFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.IdDescAdminPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRepository;

public class PaginationAdminService extends CommonPaginationService<
    AdminModel,
    AdminFilter,
    AdminPaginationParameter,
    Long,
    Admin> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminService(
        PaginationAdminRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminCommonPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected AdminModel convertEntity(Admin user) {
        return entityToModelConverter.toModel(user);
    }

    @Override
    protected AdminPaginationParameter defaultPaginationParameter() {
        return new IdDescAdminPaginationParameter();
    }
}
