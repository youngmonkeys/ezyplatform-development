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
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.model.UserAccessTokenModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescUserAccessTokenPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserAccessTokenFilter;
import org.youngmonkeys.ezyplatform.pagination.UserAccessTokenPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserAccessTokenPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserAccessTokenRepository;

public class PaginationUserAccessTokenService
    extends CommonPaginationService<
        UserAccessTokenModel,
        UserAccessTokenFilter,
        UserAccessTokenPaginationParameter,
        String,
        UserAccessToken> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserAccessTokenService(
        PaginationUserAccessTokenRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserAccessTokenPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected UserAccessTokenModel convertEntity(UserAccessToken entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserAccessTokenPaginationParameter defaultPaginationParameter() {
        return new IdDescUserAccessTokenPaginationParameter();
    }
}
