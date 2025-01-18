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
import com.tvd12.ezyfox.util.Next;
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.result.*;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends EzyDatabaseRepository<Long, User> {

    @EzyQuery(
        "SELECT DISTINCT e.status as status FROM User e"
    )
    List<StatusResult> findAllUserStatuses();

    @EzyQuery(
        "SELECT e.id FROM User e " +
            "WHERE e.uuid = ?0"
    )
    IdResult findUserIdByUuid(String uuid);

    @EzyQuery(
        "SELECT e.id " +
            "FROM User e WHERE e.id > ?0"
    )
    List<IdResult> findIdByIdGt(
        long idGt,
        Next next
    );

    @EzyQuery(
        "SELECT e.id, e.uuid FROM User e " +
            "WHERE e.uuid in ?0"
    )
    List<IdUuidResult> findUserIdsByUuids(Collection<String> uuids);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM User e WHERE e.id = ?0"
    )
    IdNameResult findUserIdAndNameById(long id);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM User e WHERE e.id in ?0"
    )
    List<IdNameResult> findUserIdAndNameByIds(Collection<Long> ids);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM User e WHERE e.username in ?0"
    )
    List<IdNameResult> findUserIdAndNameByUsernames(
        Collection<String> usernames
    );

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM User e " +
            "WHERE e.id = ?0"
    )
    IdUuidNameResult findUserUuidNameById(long id);

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM User e " +
            "WHERE e.id in ?0"
    )
    List<IdUuidNameResult> findUserUuidNamesByIds(
        Collection<Long> ids
    );

    @EzyQuery(
        "SELECT e.id, e.uuid, e.displayName " +
            "FROM User e " +
            "WHERE e.uuid = ?0"
    )
    IdUuidNameResult findUserUuidNameByUuid(String uuid);

    long countByStatus(String status);
}
