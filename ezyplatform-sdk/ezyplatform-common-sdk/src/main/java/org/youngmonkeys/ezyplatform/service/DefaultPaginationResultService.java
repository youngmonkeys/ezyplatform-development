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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.repo.PaginationResultRepository;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperation;
import org.youngmonkeys.ezyplatform.rx.RxOperationSupplier;
import org.youngmonkeys.ezyplatform.rx.RxSingle;

import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.LIMIT_1_000_000_RECORDS;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_PAGINATION_COUNT_LIMIT;
import static org.youngmonkeys.ezyplatform.util.Numbers.toIntOrZero;

/**
 * For pagination business.
 *
 * @param <T> the output type
 * @param <F> the filter value type
 * @param <P> the pagination value type (inclusive or exclusive value type)
 * @param <I> the id type
 * @param <E> the entity type
 * @param <R> the query result type
 */
@AllArgsConstructor
public abstract class DefaultPaginationResultService<T, F, P, I, E, R>
    extends PaginationService<T, F, P> {

    protected final PaginationResultRepository<F, P, I, E, R> repository;

    @Override
    protected RxOperation getFirstItems(F filter, int limit) {
        return convertEntities(
            repository.findFirstElements(filter, limit)
        );
    }

    @Override
    protected RxOperation getNextItemsExclusive(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return convertEntities(
            repository.findNextElements(filter, paginationParameter, limit)
        );
    }

    @Override
    protected RxOperation getLastItems(F filter, int limit) {
        return convertEntities(
            repository.findLastElements(filter, limit)
        );
    }

    @Override
    protected RxOperation getPreviousItemsExclusive(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return convertEntities(
            repository.findPreviousElements(filter, paginationParameter, limit)
        );
    }

    @Override
    protected long getTotalItems(F filter) {
        if (allowCountAllItems()) {
            return repository.countElements(filter);
        }
        int countLimit = getCountLimit();
        List<R> items = repository.findFirstElements(
            filter,
            countLimit,
            1
        );
        return items.isEmpty()
            ? repository.countElements(filter)
            : countLimit;
    }

    protected boolean allowCountAllItems() {
        return false;
    }

    protected int getCountLimit() {
        String value = repository.findSettingValue(
            SETTING_NAME_PAGINATION_COUNT_LIMIT
        );
        int limit = 0;
        if (isNotBlank(value)) {
            limit = toIntOrZero(value);
        }
        if (limit <= 0) {
            limit = LIMIT_1_000_000_RECORDS;
        }
        return limit;
    }

    protected RxOperation convertEntities(List<R> entities) {
        RxSingle<R> single = Reactive.single(entities);
        RxOperationSupplier<R> supplier = convertEntityRxSupplier();
        if (supplier != null) {
            single.mapItemRx(supplier);
        } else {
            single.mapItem(this::convertEntity);
        }
        return single;
    }

    @SuppressWarnings("unchecked")
    protected T convertEntity(R entity) {
        return (T) entity;
    }

    protected RxOperationSupplier<R> convertEntityRxSupplier() {
        return null;
    }
}
