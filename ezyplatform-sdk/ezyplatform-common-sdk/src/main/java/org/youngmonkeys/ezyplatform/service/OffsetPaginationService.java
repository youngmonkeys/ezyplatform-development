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

import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;
import org.youngmonkeys.ezyplatform.pagination.OffsetPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationRepository;

import java.util.function.Function;

/**
 * For pagination business.
 *
 * @param <T> the output type
 * @param <F> the filter value type
 * @param <P> the pagination value type (inclusive or exclusive value type)
 * @param <I> the id type
 * @param <E> the entity type
 */
public class OffsetPaginationService<
    T,
    F extends CommonStorageFilter,
    P extends OffsetPaginationParameter,
    I,
    E
    > extends DefaultPaginationService<T, F, P, I, E> {

    private final Function<Long, P> paginationParameterCreator;

    public OffsetPaginationService(
        PaginationRepository<F, P, I, E> repository,
        Function<Long, P> paginationParameterCreator
    ) {
        super(repository);
        this.paginationParameterCreator = paginationParameterCreator;
    }

    @Override
    protected String serializeToPageToken(
        P paginationParameter,
        T model
    ) {
        return String.valueOf(paginationParameter.getOffset());
    }

    @Override
    protected P deserializePageToken(String value) {
        return paginationParameterCreator.apply(
            Long.valueOf(value)
        );
    }

    @Override
    protected P defaultFirstPaginationParameter() {
        return paginationParameterCreator.apply(0L);
    }

    @Override
    protected P defaultLastPaginationParameter(F filter, int limit) {
        return paginationParameterCreator.apply(
            getTotalItems(filter) - limit
        );
    }
}
