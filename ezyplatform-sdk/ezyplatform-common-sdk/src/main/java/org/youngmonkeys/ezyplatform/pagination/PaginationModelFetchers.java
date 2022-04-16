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

package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.service.PaginationService;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class PaginationModelFetchers {

    private PaginationModelFetchers() {}

    @SuppressWarnings({"rawtypes"})
    public static <T> PaginationModel<T> getPaginationModel(
        PaginationService paginationService,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        return getPaginationModel(
            paginationService,
            null,
            nextPageToken,
            prevPageToken,
            lastPage,
            limit
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> PaginationModel<T> getPaginationModel(
        PaginationService paginationService,
        Object filter,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        if (lastPage) {
            return paginationService.getLastPage(filter, limit);
        }
        if (isBlank(prevPageToken)) {
            return paginationService.getNextPage(filter, nextPageToken, limit);
        }
        return paginationService.getPreviousPage(filter, prevPageToken, limit);
    }
}
