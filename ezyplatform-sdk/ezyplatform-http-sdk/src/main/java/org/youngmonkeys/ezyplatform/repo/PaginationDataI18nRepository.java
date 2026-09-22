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

package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.entity.DataI18nId;
import org.youngmonkeys.ezyplatform.pagination.DataI18nFilter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationDataI18nRepository extends CommonPaginationRepository<
    DataI18nFilter,
    DataI18nPaginationParameter,
    DataI18nId,
    DataI18n> {

    @Override
    protected Class<DataI18n> getEntityType() {
        return DataI18n.class;
    }
}
