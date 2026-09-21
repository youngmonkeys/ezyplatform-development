/*
 * Copyright 2026 youngmonkeys.org
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

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByDesc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionDesc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataTypeDescDataIdDescLanguageDescFieldNameDescDataI18nPaginationParameter
    implements DataI18nPaginationParameter {

    public String dataType;
    public Long dataId;
    public String language;
    public String fieldName;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "dataType",
                "dataId",
                "language",
                "fieldName"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(
            nextPage,
            "dataType", "dataId", "language", "fieldName"
        );
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(dataType, dataId, language, fieldName);
    }

    @Override
    public String sortOrder() {
        return DataI18nPaginationSortOrder
            .DATA_TYPE_DESC_DATA_ID_DESC_LANGUAGE_DESC_FIELD_NAME_DESC
            .toString();
    }
}
