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

package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.pagination.UserFilter;
import org.youngmonkeys.ezyplatform.pagination.UserPaginationParameter;

public class PaginationUserRepository extends PaginationRepository<
    UserFilter,
    UserPaginationParameter,
    Long,
    User> {

    @Override
    protected boolean isDistinct() {
        return true;
    }

    @Override
    protected String makeMatchingCondition(UserFilter filter) {
        return filter.matchingCondition();
    }

    @Override
    protected void decorateQueryStringBeforeWhere(
        StringBuilder queryString,
        UserFilter filter
    ) {
        filter.decorateQueryStringBeforeWhere(queryString);
    }

    @Override
    protected String makePaginationCondition(
        UserPaginationParameter paginationParameter,
        boolean nextPage
    ) {
        return paginationParameter.paginationCondition(nextPage);
    }

    @Override
    protected String makeOrderBy(
        UserPaginationParameter paginationParameter,
        boolean nextPage
    ) {
        return paginationParameter.orderBy(nextPage);
    }

    @Override
    protected Class<User> getEntityType() {
        return User.class;
    }
}
