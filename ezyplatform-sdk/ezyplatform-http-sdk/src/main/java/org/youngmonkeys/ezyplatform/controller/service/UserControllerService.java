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

package org.youngmonkeys.ezyplatform.controller.service;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.model.KeywordsModel;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.pagination.DefaultUserFilter;
import org.youngmonkeys.ezyplatform.pagination.UserPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.service.PaginationUserService;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.pagination.PaginationModelFetchers.getPaginationModelBySortOrder;
import static org.youngmonkeys.ezyplatform.util.StringConverters.trimOrNull;

@AllArgsConstructor
public class UserControllerService {

    private final PaginationUserService paginationUserService;
    private final UserPaginationParameterConverter userPaginationParameterConverter;

    public PaginationModel<UserModel> getUserPagination(
        String keyword,
        boolean allowSearchUserByLikeOperator,
        DefaultUserFilter.Builder<?> filterBuilder,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        return getUserPagination(
            keyword,
            allowSearchUserByLikeOperator,
            filterBuilder,
            NULL_STRING,
            nextPageToken,
            prevPageToken,
            lastPage,
            limit
        );
    }

    public PaginationModel<UserModel> getUserPagination(
        String keyword,
        boolean allowSearchUserByLikeOperator,
        DefaultUserFilter.Builder<?> filterBuilder,
        String sortOrder,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        PaginationModel<UserModel> pagination =
            PaginationModel.emptyPagination();
        String nullableKeyword = trimOrNull(keyword);
        if (nullableKeyword == null) {
            pagination = getPaginationModelBySortOrder(
                paginationUserService,
                userPaginationParameterConverter,
                filterBuilder.build(),
                sortOrder,
                nextPageToken,
                prevPageToken,
                lastPage,
                limit
            );
        }
        if (nullableKeyword != null) {
            pagination = getPaginationModelBySortOrder(
                paginationUserService,
                userPaginationParameterConverter,
                filterBuilder
                    .uniqueKeyword(nullableKeyword)
                    .build(),
                sortOrder,
                nextPageToken,
                prevPageToken,
                lastPage,
                limit
            );
            if (pagination.getCount() == ZERO) {
                KeywordsModel keywords = KeywordsModel.extract(
                    keyword,
                    allowSearchUserByLikeOperator
                );
                pagination = getPaginationModelBySortOrder(
                    paginationUserService,
                    userPaginationParameterConverter,
                    filterBuilder
                        .uniqueKeyword(NULL_STRING)
                        .likeKeyword(keywords.getLikeKeyword())
                        .keywords(keywords.getKeywords())
                        .build(),
                    sortOrder,
                    nextPageToken,
                    prevPageToken,
                    lastPage,
                    limit
                );
            }
        }
        return pagination;
    }
}
