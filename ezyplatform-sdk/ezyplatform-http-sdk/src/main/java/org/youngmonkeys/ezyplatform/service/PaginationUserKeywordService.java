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
import org.youngmonkeys.ezyplatform.entity.UserKeyword;
import org.youngmonkeys.ezyplatform.model.UserKeywordModel;
import org.youngmonkeys.ezyplatform.pagination.PriorityDescIdDescUserKeywordPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserKeywordFilter;
import org.youngmonkeys.ezyplatform.pagination.UserKeywordPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserKeywordPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserKeywordRepository;

public class PaginationUserKeywordService extends CommonPaginationService<
    UserKeywordModel,
    UserKeywordFilter,
    UserKeywordPaginationParameter,
    Long,
    UserKeyword> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserKeywordService(
        PaginationUserKeywordRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserKeywordPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected UserKeywordModel convertEntity(UserKeyword entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserKeywordPaginationParameter defaultPaginationParameter() {
        return new PriorityDescIdDescUserKeywordPaginationParameter();
    }
}
