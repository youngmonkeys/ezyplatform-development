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
import org.youngmonkeys.ezyplatform.entity.UserMeta;
import org.youngmonkeys.ezyplatform.entity.UserMeta;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserMetaRepository
    extends EzyDatabaseRepository<Long, UserMeta> {

    List<UserMeta> findByUserId(long userId);

    Optional<UserMeta> findByMetaKeyAndMetaValue(
        String metaKey,
        String metaValue
    );

    Optional<UserMeta> findByUserIdAndMetaKey(
        long userId,
        String metaKey
    );

    List<UserMeta> findByUserIdAndMetaKey(
        long userId,
        String metaKey,
        Next next
    );

    @EzyQuery(
        "SELECT e FROM UserMeta e " +
            "WHERE e.userId = ?0 AND e.metaKey = ?1 " +
            "ORDER BY e.id DESC"
    )
    Optional<UserMeta> findByUserIdAndMetaKeyOrderByIdDesc(
        long userId,
        String metaKey
    );

    Optional<UserMeta> findByUserIdAndMetaKeyAndMetaValue(
        long userId,
        String metaKey,
        String metaValue
    );

    List<UserMeta> findByMetaKeyAndMetaValueIn(
        String metaKey,
        Collection<String> metaValues
    );

    List<UserMeta> findByUserIdInAndMetaKey(
        Collection<Long> userIds,
        String metaKey
    );
}
