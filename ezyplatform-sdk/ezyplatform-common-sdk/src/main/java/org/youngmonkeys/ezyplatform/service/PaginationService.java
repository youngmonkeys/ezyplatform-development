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
import org.youngmonkeys.ezyplatform.data.PaginationParameter;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.pagination.OffsetPaginationParameter;
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
        if (paginationParameter == null) {
            paginationParameter = defaultFirstPaginationParameter();
        }
        P paginationParameterFinal = paginationParameter;
        int limitPlusOne = limit + 1;
        return Reactive.multiple()
            .registerRx(
                "listPlusOne",
                getNextItems(filter, paginationParameterFinal, limitPlusOne)
            )
            .register(
                "total",
                () -> getTotalItems(filter)
            )
            .blockingGet(map ->
                toNextPageModel(map, paginationParameterFinal, limit)
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
        long tmpTotalItems = -1L;
        P paginationParameter = doDeserializePageToken(pageToken);
        if (paginationParameter == null) {
            tmpTotalItems = getTotalItems(filter);
            paginationParameter = defaultLastPaginationParameter(
                tmpTotalItems,
                limit
            );
        }
        P paginationParameterFinal = paginationParameter;
        int limitPlusOne = limit + 1;
        final long totalItems = tmpTotalItems;
        return Reactive.multiple()
            .registerRx(
                "listPlusOne",
                getPreviousItems(filter, paginationParameterFinal, limitPlusOne)
            )
            .register(
                "total",
                () -> totalItems >= 0 ? totalItems : getTotalItems(filter)
            )
            .blockingGet(map ->
                toPreviousPageModel(map, paginationParameterFinal, limit)
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
        boolean hasPrev = !isEmptyPaginationParameter(
            paginationParameter
        ) && list.size() > 0;
        T nextPageTokenItem = hasNext ? EzyLists.last(list) : null;
        T lastPageTokenItem =  hasPrev ? EzyLists.first(list) : null;
        PaginationModel.PageToken pageToken;
        if (paginationParameter instanceof OffsetPaginationParameter) {
            OffsetPaginationParameter offsetPaginationParameter =
                (OffsetPaginationParameter) paginationParameter;
            pageToken = PaginationModel.PageToken
                .builder()
                .next(
                    doSerializeToOffsetPageToken(
                        offsetPaginationParameter.nextOffset(limit),
                        nextPageTokenItem
                    )
                )
                .prev(
                    doSerializeToOffsetPageToken(
                        offsetPaginationParameter.previousOffset(limit),
                        lastPageTokenItem
                    )
                )
                .build();
        } else {
            pageToken = PaginationModel.PageToken
                .builder()
                .next(doSerializeToPageToken(paginationParameter, nextPageTokenItem))
                .prev(doSerializeToPageToken(paginationParameter, lastPageTokenItem))
                .build();
        }
        return PaginationModel.<T>builder()
            .items(list)
            .count(list.size())
            .total(map.get("total"))
            .timestamp(getTimestamp())
            .pageToken(pageToken)
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(hasNext)
                    .hasPrevious(hasPrev)
                    .build()
            )
            .build();
    }

    @SuppressWarnings("MethodLength")
    private PaginationModel<T> toPreviousPageModel(
        RxValueMap map,
        P paginationParameter,
        int limit
    ) {
        List<T> list;
        boolean hasNext;
        boolean hasPrev;
        OffsetPaginationParameter offsetPaginationParameter = null;
        if (paginationParameter instanceof OffsetPaginationParameter) {
            offsetPaginationParameter =
                (OffsetPaginationParameter) paginationParameter;
        }
        List<T> listPlusOne = map.get("listPlusOne");
        if (offsetPaginationParameter != null) {
            int offset = offsetPaginationParameter.getOffset();
            int count = offset >= 0 ? limit : Math.max(offset + limit, 0);
            list = EzyLists.take(listPlusOne, count);
            hasNext = listPlusOne.size() > count;
            hasPrev = offset > 0;
        } else {
            list = EzyLists.take(listPlusOne, limit);
            hasNext = !isEmptyPaginationParameter(
                paginationParameter
            ) && list.size() > 0;
            hasPrev = listPlusOne.size() > limit;
        }
        T nextPageTokenItem = hasNext ? EzyLists.first(list) : null;
        T lastPageTokenItem =  hasPrev ? EzyLists.last(list) : null;
        PaginationModel.PageToken pageToken;
        if (offsetPaginationParameter != null) {
            pageToken = PaginationModel.PageToken
                .builder()
                .next(
                    doSerializeToOffsetPageToken(
                        offsetPaginationParameter.nextOffset(limit),
                        nextPageTokenItem
                    )
                )
                .prev(
                    doSerializeToOffsetPageToken(
                        offsetPaginationParameter.previousOffset(limit),
                        lastPageTokenItem
                    )
                )
                .build();
        } else {
            Collections.reverse(list);
            pageToken = PaginationModel.PageToken
                .builder()
                .next(doSerializeToPageToken(paginationParameter, nextPageTokenItem))
                .prev(doSerializeToPageToken(paginationParameter, lastPageTokenItem))
                .build();
        }
        long totalItems = map.get("total");
        return PaginationModel.<T>builder()
            .items(list)
            .count(list.size())
            .total(totalItems)
            .timestamp(getTimestamp())
            .pageToken(pageToken)
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

    private String doSerializeToOffsetPageToken(
        long offset,
        T value
    ) {
        return value == null
            ? null
            : EzyBase64.encodeUtf(String.valueOf(offset));
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
            ? defaultPaginationParameter()
            : deserializePageToken(EzyBase64.decodeUtf(pageToken));
    }

    protected P defaultPaginationParameter() {
        return null;
    }

    protected P defaultFirstPaginationParameter() {
        return null;
    }

    protected P defaultLastPaginationParameter(
        long totalItems,
        int limit
    ) {
        return null;
    }

    protected abstract P deserializePageToken(String pageToken);

    protected boolean isEmptyPaginationParameter(
        Object paginationParameter
    ) {
        if (paginationParameter == null) {
            return true;
        }
        if (paginationParameter instanceof PaginationParameter) {
            return ((PaginationParameter) paginationParameter)
                .isEmpty();
        }
        return false;
    }
}
