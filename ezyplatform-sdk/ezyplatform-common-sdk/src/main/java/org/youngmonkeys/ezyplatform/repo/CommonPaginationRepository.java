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

import org.youngmonkeys.ezyplatform.pagination.CommonPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

/**
 * For pagination business.
 *
 * @param <F> the filter value type
 * @param <P> the pagination value type (inclusive or exclusive value type)
 * @param <I> the id type
 * @param <E> the entity type
 */
public class CommonPaginationRepository<
    F extends CommonStorageFilter,
    P extends CommonPaginationParameter,
    I,
    E> extends PaginationRepository<F, P, I, E> {

    @Override
    protected boolean isDistinct() {
        return true;
    }

    @Override
    protected String makeMatchingCondition(F filter) {
        return filter != null
            ? filter.matchingCondition()
            : EMPTY_STRING;
    }

    @Override
    protected void decorateQueryStringBeforeWhere(
        StringBuilder queryString,
        F filter
    ) {
        if (filter != null) {
            filter.decorateQueryStringBeforeWhere(queryString);
        }
    }

    @Override
    protected String makePaginationCondition(
        P paginationParameter,
        boolean nextPage
    ) {
        return paginationParameter.paginationCondition(nextPage);
    }

    @Override
    protected String makeOrderBy(
        P paginationParameter,
        boolean nextPage
    ) {
        return paginationParameter.orderBy(nextPage);
    }
}
