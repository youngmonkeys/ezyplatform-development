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
import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.entity.UserRoleId;

import java.util.Collection;

public interface UserRoleRepository
    extends EzyDatabaseRepository<UserRoleId, UserRole> {

    void deleteByUserId(long userId);

    void deleteByUserIdIn(Collection<Long> userIds);

    @EzyQuery(
        "SELECT ur FROM UserRole ur " +
            "INNER JOIN User u ON ur.userId = u.id " +
            "INNER JOIN UserRoleName r ON ur.roleId = r.id " +
            "WHERE u.username = ?0 AND r.name = ?1"
    )
    UserRole findByUsernameAndRoleName(
        String username,
        String roleName
    );

    int countByRoleId(long roleId);
}
