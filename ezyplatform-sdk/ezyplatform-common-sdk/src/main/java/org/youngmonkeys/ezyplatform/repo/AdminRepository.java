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

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import org.youngmonkeys.ezyplatform.entity.Admin;
import org.youngmonkeys.ezyplatform.result.IdNameResult;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.result.IdUuidNameResult;

import java.util.Collection;
import java.util.List;

public interface AdminRepository extends EzyDatabaseRepository<Long, Admin> {

    @EzyQuery(
        "SELECT e.id FROM Admin e " +
            "WHERE e.uuid = ?0"
    )
    IdResult findAdminIdByUuid(String uuid);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM Admin e " +
            "WHERE e.id = ?0"
    )
    IdNameResult findAdminNameById(long id);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM Admin e " +
            "WHERE e.id in ?0"
    )
    List<IdNameResult> findAdminNamesByIds(
        Collection<Long> ids
    );

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM Admin e " +
            "WHERE e.id = ?0"
    )
    IdUuidNameResult findAdminUuidNameById(long id);

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM Admin e " +
            "WHERE e.id in ?0"
    )
    List<IdUuidNameResult> findAdminUuidNamesByIds(
        Collection<Long> ids
    );

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM Admin e " +
            "WHERE e.uuid = ?0"
    )
    IdUuidNameResult findAdminUuidNameByUuid(String uuid);
}
