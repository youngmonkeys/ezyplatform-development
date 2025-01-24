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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdDescSettingPaginationParameter
    implements SettingPaginationParameter {

    public Long id;

    @Override
    public String paginationCondition(boolean nextPage) {
        if (id == null) {
            return null;
        }
        return nextPage
            ? "e.id < :id"
            : "e.id > :id";
    }

    @Override
    public String orderBy(boolean nextPage) {
        return nextPage
            ? "e.id DESC"
            : "e.id ASC";
    }

    @Override
    public String sortOrder() {
        return SettingPaginationSortOrder.ID_DESC.toString();
    }
}
