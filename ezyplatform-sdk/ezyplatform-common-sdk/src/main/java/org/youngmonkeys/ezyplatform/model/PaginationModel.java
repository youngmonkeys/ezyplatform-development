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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@Getter
@Builder
@ToString
public class PaginationModel<T> {

    private List<T> items;
    private PageToken pageToken;
    private Continuation continuation;
    private int count;
    private long total;
    private long timestamp;

    @SuppressWarnings("rawtypes")
    public static final PaginationModel EMPTY_PAGINATION =
        PaginationModel.builder()
            .pageToken(PageToken.builder().build())
            .continuation(Continuation.builder().build())
            .items(Collections.emptyList())
            .build();

    @SuppressWarnings("unchecked")
    public static <T> PaginationModel<T> emptyPagination() {
        return EMPTY_PAGINATION;
    }

    public <R> PaginationModel<R> map(
        Function<T, R> mapper
    ) {
        return PaginationModel.<R>builder()
            .items(newArrayList(items, mapper))
            .pageToken(pageToken)
            .continuation(continuation)
            .count(count)
            .total(total)
            .timestamp(timestamp)
            .build();
    }

    @Getter
    @Builder
    @ToString
    public static class PageToken {
        private String next;
        private String prev;
    }

    @Getter
    @Builder
    @ToString
    public static class Continuation {
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
