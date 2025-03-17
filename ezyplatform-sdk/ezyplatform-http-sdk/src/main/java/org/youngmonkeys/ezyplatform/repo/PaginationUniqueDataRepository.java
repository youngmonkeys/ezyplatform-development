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

package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.UniqueData;
import org.youngmonkeys.ezyplatform.entity.UniqueDataId;
import org.youngmonkeys.ezyplatform.pagination.UniqueDataFilter;
import org.youngmonkeys.ezyplatform.pagination.UniqueDataPaginationParameter;

public class PaginationUniqueDataRepository extends CommonPaginationRepository<
    UniqueDataFilter,
    UniqueDataPaginationParameter,
    UniqueDataId,
    UniqueData> {

    @Override
    protected Class<UniqueData> getEntityType() {
        return UniqueData.class;
    }
}
