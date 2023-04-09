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

public class PaginationUserService extends DefaultPaginationService<
    UserModel,
    UserFilter,
    UserPaginationParameter,
    Long,
    User> {

    private final DefaultEntityToModelConverter entityToModelConverter;
    private final UserPaginationParameterConverter userPaginationParameterConverter;

    public PaginationUserService(
        PaginationUserRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserPaginationParameterConverter userPaginationParameterConverter
    ) {
        super(repository);
        this.entityToModelConverter = entityToModelConverter;
        this.userPaginationParameterConverter = userPaginationParameterConverter;
    }

    @Override
    protected UserModel convertEntity(User user) {
        return entityToModelConverter.toModel(user);
    }

    @Override
    protected String serializeToPageToken(
        UserPaginationParameter paginationParameter,
        UserModel model
    ) {
        return userPaginationParameterConverter.serialize(
            paginationParameter.sortOrder(),
            model
        );
    }

    @Override
    protected UserPaginationParameter deserializePageToken(String value) {
        return userPaginationParameterConverter.deserialize(
            value
        );
    }

    @Override
    protected UserPaginationParameter defaultPaginationParameter() {
        return new IdDescUserPaginationParameter(0L);
    }
}
