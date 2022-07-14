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

import com.tvd12.ezyfox.io.EzyLists;
import com.tvd12.ezyfox.security.EzyBase64;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperation;
import org.youngmonkeys.ezyplatform.rx.RxValueMap;

import java.util.Collections;
import java.util.List;

/**
 * For pagination business.
 *
 * @param <T> the item type
 * @param <F> the filter value type
 * @param <P> the pagination value type (inclusive or exclusive value type)
 */
public abstract class PaginationService<T, F, P> {

    public final PaginationModel<T> getFirstPage(int limit) {
        return getFirstPage(null, limit);
    }

    public final PaginationModel<T> getFirstPage(
        F filter,
        int limit
    ) {
        return getNextPage(filter, null, limit);
    }

    public final PaginationModel<T> getNextPage(
        String pageToken,
        int limit
    ) {
        return getNextPage(null, pageToken, limit);
    }

    public final PaginationModel<T> getNextPage(
        F filter,
        String pageToken,
        int limit
    ) {
        P paginationParameter = doDeserializePageToken(pageToken);
        int limitPlusOne = limit + 1;
        return Reactive.multiple()
            .registerRx(
                "listPlusOne",
                getNextItems(filter, paginationParameter, limitPlusOne)
            )
            .register(
                "total",
                () -> getTotalItems(filter)
            )
            .blockingGet(map ->
                toNextPageModel(map, paginationParameter, limit)
            );
    }

    public final PaginationModel<T> getLastPage(int limit) {
        return getLastPage(null, limit);
    }

    public final PaginationModel<T> getLastPage(
        F filter,
        int limit
    ) {
        return getPreviousPage(filter, null, limit);
    }

    public final PaginationModel<T> getPreviousPage(
        String pageToken,
        int limit
    ) {
        return getPreviousPage(null, pageToken, limit);
    }

    public final PaginationModel<T> getPreviousPage(
        F filter,
        String pageToken,
        int limit
    ) {
        P paginationParameter = doDeserializePageToken(pageToken);
        int limitPlusOne = limit + 1;
        return Reactive.multiple()
            .registerRx(
                "listPlusOne",
                getPreviousItems(filter, paginationParameter, limitPlusOne)
            )
            .register(
                "total",
                () -> getTotalItems(filter)
            )
            .blockingGet(map ->
                toPreviousPageModel(map, paginationParameter, limit)
            );
    }

    private PaginationModel<T> toNextPageModel(
        RxValueMap map,
        P paginationParameter,
        int limit
    ) {
        List<T> listPlusOne = map.get("listPlusOne");
        List<T> list = EzyLists.take(listPlusOne, limit);
        boolean hasNext = listPlusOne.size() > limit;
        boolean hasPrev = paginationParameter != null && list.size() > 0;
        T nextPageTokenItem = hasNext ? EzyLists.last(list) : null;
        T lastPageTokenItem =  hasPrev ? EzyLists.first(list) : null;
        return PaginationModel.<T>builder()
            .items(list)
            .count(list.size())
            .total(map.get("total"))
            .timestamp(getTimestamp())
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next(doSerializeToPageToken(paginationParameter, nextPageTokenItem))
                    .prev(doSerializeToPageToken(paginationParameter, lastPageTokenItem))
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(hasNext)
                    .hasPrevious(hasPrev)
                    .build()
            )
            .build();
    }

    private PaginationModel<T> toPreviousPageModel(
        RxValueMap map,
        P paginationParameter,
        int limit
    ) {
        List<T> listPlusOne = map.get("listPlusOne");
        List<T> list = EzyLists.take(listPlusOne, limit);
        boolean hasNext = paginationParameter != null && list.size() > 0;
        boolean hasPrev = listPlusOne.size() > limit;
        T nextPageTokenItem = hasNext ? EzyLists.first(list) : null;
        T lastPageTokenItem =  hasPrev ? EzyLists.last(list) : null;
        Collections.reverse(list);
        return PaginationModel.<T>builder()
            .items(list)
            .count(list.size())
            .total(map.get("total"))
            .timestamp(getTimestamp())
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next(doSerializeToPageToken(paginationParameter, nextPageTokenItem))
                    .prev(doSerializeToPageToken(paginationParameter, lastPageTokenItem))
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(hasNext)
                    .hasPrevious(hasPrev)
                    .build()
            )
            .build();
    }

    private RxOperation getNextItems(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return paginationParameter == null
            ? getFirstItems(filter, limit)
            : getNextItemsExclusive(filter, paginationParameter, limit);
    }

    protected abstract RxOperation getFirstItems(F filter, int limit);

    protected abstract RxOperation getNextItemsExclusive(
        F filter,
        P paginationParameter,
        int limit
    );

    protected abstract RxOperation getPreviousItemsExclusive(
        F filter,
        P paginationParameter,
        int limit
    );

    private RxOperation getPreviousItems(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return paginationParameter == null
            ? getLastItems(filter, limit)
            : getPreviousItemsExclusive(filter, paginationParameter, limit);
    }

    protected abstract RxOperation getLastItems(F filter, int limit);

    protected abstract long getTotalItems(F filter);

    protected long getTimestamp() {
        return System.currentTimeMillis();
    }

    private String doSerializeToPageToken(
        P paginationParameter,
        T value
    ) {
        return value == null
            ? null
            : EzyBase64.encodeUtf(serializeToPageToken(paginationParameter, value));
    }

    protected String serializeToPageToken(
        P paginationParameter,
        T value
    ) {
        return serializeToPageToken(value);
    }

    protected String serializeToPageToken(T value) {
        return null;
    }

    private P doDeserializePageToken(String pageToken) {
        return pageToken == null
            ? null
            : deserializePageToken(EzyBase64.decodeUtf(pageToken));
    }

    protected abstract P deserializePageToken(String pageToken);
}
