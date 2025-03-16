/*
 * Copyright 2023 youngmonkeys.org
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByAsc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionAsc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MappedAtAscToDataIdAscDataMappingPaginationParameter
    implements DataMappingPaginationParameter {

    public LocalDateTime mappedAt;
    public Long toDataId;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "mappedAt",
                "toDataId"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(nextPage, "mappedAt", "toDataId");
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(mappedAt, toDataId);
    }

    @Override
    public String sortOrder() {
        return DataMappingPaginationSortOrder
            .MAPPED_AT_ASC_TO_DATA_ID_ASC
            .toString();
    }
}
