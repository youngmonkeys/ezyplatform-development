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
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescUserPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserFilter;
import org.youngmonkeys.ezyplatform.pagination.UserPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserRepository;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_USER;

public class PaginationUserService extends CommonPaginationService<
    UserModel,
    UserFilter,
    UserPaginationParameter,
    Long,
    User> {

    private final DataRecordCountService dataRecordCountService;
    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserService(
        DataRecordCountService dataRecordCountService,
        PaginationUserRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.dataRecordCountService = dataRecordCountService;
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected long getTotalItems(UserFilter filter) {
        return dataRecordCountService.getRecordCount(
            TABLE_NAME_USER
        );
    }

    @Override
    protected UserModel convertEntity(User user) {
        return entityToModelConverter.toModel(user);
    }

    @Override
    protected UserPaginationParameter defaultPaginationParameter() {
        return new IdDescUserPaginationParameter();
    }
}
