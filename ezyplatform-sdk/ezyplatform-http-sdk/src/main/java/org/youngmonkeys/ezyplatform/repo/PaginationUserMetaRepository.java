/*
 * Copyright 2025 youngmonkeys.org
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

import org.youngmonkeys.ezyplatform.entity.UserMeta;
import org.youngmonkeys.ezyplatform.pagination.UserMetaFilter;
import org.youngmonkeys.ezyplatform.pagination.UserMetaPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationUserMetaRepository extends CommonPaginationRepository<
    UserMetaFilter,
    UserMetaPaginationParameter,
    Long,
    UserMeta> {

    @Override
    protected Class<UserMeta> getEntityType() {
        return UserMeta.class;
    }
}
