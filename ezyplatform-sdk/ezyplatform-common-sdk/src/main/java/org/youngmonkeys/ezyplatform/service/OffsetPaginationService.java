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

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.OffsetPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationRepository;

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

    private final ComplexPaginationParameterConverter<String, P>
        paginationParameterConverter;

    public OffsetPaginationService(
        PaginationRepository<F, P, I, E> repository,
        ComplexPaginationParameterConverter<String, P>
            paginationParameterConverter
    ) {
        super(repository);
        this.paginationParameterConverter = paginationParameterConverter;
    }

    @Override
    protected boolean allowCountAllItems() {
        return true;
    }

    @Override
    protected String serializeToPageToken(
        P paginationParameter,
        T model
    ) {
        return paginationParameterConverter.serialize(
            paginationParameter.sortOrder(),
            paginationParameter
        );
    }

    @Override
    protected P deserializePageToken(String value) {
        return paginationParameterConverter.deserialize(
            value
        );
    }

    @Override
    protected P defaultPaginationParameter() {
        throw new EzyNotImplementedException(
            "need to implement defaultPaginationParameter method"
        );
    }
}
