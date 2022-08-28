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
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.result.UserNameResult;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends EzyDatabaseRepository<Long, User> {

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM User e WHERE e.id = ?0"
    )
    UserNameResult findUserIdAndNameById(long id);

    @EzyQuery(
        "SELECT e.id, e.username, e.displayName " +
            "FROM User e WHERE e.id in ?0"
    )
    List<UserNameResult> findUserIdAndNameByIds(Collection<Long> ids);
}
