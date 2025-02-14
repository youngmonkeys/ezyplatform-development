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
            "WHERE e.username = ?0"
    )
    IdResult findUserIdByUsername(String username);

    @EzyQuery(
        "SELECT e.id FROM User e " +
            "WHERE e.email = ?0"
    )
    IdResult findUserIdByEmail(String email);

    @EzyQuery(
        "SELECT e.id FROM User e " +
            "WHERE e.phone = ?0"
    )
    IdResult findUserIdByPhone(String phone);

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

    List<User> findByUsernameIn(Collection<String> usernames);

    List<User> findByPhoneIn(Collection<String> phones);

    List<User> findByEmailIn(Collection<String> emails);

    List<User> findByUuidIn(Collection<String> uuid);

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "WHERE e.phone = ?0 " +
            "OR e.email = ?0 " +
            "OR e.displayName = ?0 " +
            "OR e.username = ?0"
    )
    List<IdNameResult> findUsernameByUniqueKeyword(
        String keyword,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "WHERE e.phone IN ?0 " +
            "OR e.email IN ?0 " +
            "OR e.displayName IN ?0 " +
            "OR e.username IN ?0"
    )
    List<IdNameResult> findUsernameByUniqueKeywords(
        Collection<String> keywords,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "WHERE e.phone LIKE CONCAT('%',?0,'%') " +
            "OR e.email LIKE CONCAT('%',?0,'%') " +
            "OR e.displayName LIKE CONCAT('%',?0,'%') " +
            "OR e.username LIKE CONCAT('%',?0,'%')"
    )
    List<IdNameResult> findUsernameByKeyword(
        String keyword,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "INNER JOIN UserKeyword k ON e.id = k.userId " +
            "WHERE k.keyword in ?0"
    )
    List<IdNameResult> findUsernameByKeywords(
        Collection<String> keywords,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "INNER JOIN UserRole a ON e.id = a.userId " +
            "WHERE a.roleId in ?0 AND (" +
            "e.phone LIKE CONCAT('%',?1,'%') " +
            "OR e.email LIKE CONCAT('%',?1,'%') " +
            "OR e.displayName LIKE CONCAT('%',?1,'%') " +
            "OR e.username LIKE CONCAT('%',?1,'%')" +
            ")"
    )
    List<IdNameResult> findUsernameByKeywordAndRoleIds(
        Collection<Long> roleIds,
        String keyword,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "INNER JOIN UserRole a ON e.id = a.userId " +
            "INNER JOIN UserKeyword b ON e.id = b.userId " +
            "WHERE a.roleId in ?0 AND b.keyword in ?1"
    )
    List<IdNameResult> findUsernameByKeywordsAndRoleIds(
        Collection<Long> roleIds,
        Collection<String> keywords,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "INNER JOIN UserRole a ON e.id = a.userId " +
            "INNER JOIN UserRoleName b ON b.id = a.roleId " +
            "INNER JOIN UserKeyword c ON e.id = c.userId " +
            "WHERE b.name in ?0 AND (" +
            "e.phone LIKE CONCAT('%',?1,'%') " +
            "OR e.email LIKE CONCAT('%',?1,'%') " +
            "OR e.displayName LIKE CONCAT('%',?1,'%') " +
            "OR e.username LIKE CONCAT('%',?1,'%')" +
            ")"
    )
    List<IdNameResult> findUsernameByKeywordAndRoleNames(
        Collection<String> roleNames,
        String keyword,
        Next next
    );

    @EzyQuery(
        "SELECT " +
            "DISTINCT(e.id) as id, " +
            "e.username as username, " +
            "e.displayName as displayName " +
            "FROM User e " +
            "INNER JOIN UserRole a ON e.id = a.userId " +
            "INNER JOIN UserRoleName b ON b.id = a.roleId " +
            "INNER JOIN UserKeyword c ON e.id = c.userId " +
            "WHERE b.name in ?0 AND c.keyword in ?1"
    )
    List<IdNameResult> findUsernameByKeywordsAndRoleNames(
        Collection<String> roleNames,
        Collection<String> keywords,
        Next next
    );

    long countByStatus(String status);
}
