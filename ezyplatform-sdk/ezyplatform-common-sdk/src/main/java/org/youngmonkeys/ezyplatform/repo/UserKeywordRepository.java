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
import org.youngmonkeys.ezyplatform.entity.UserKeyword;
import org.youngmonkeys.ezyplatform.result.IdResult;

import java.util.Collection;
import java.util.List;

public interface UserKeywordRepository
    extends EzyDatabaseRepository<Long, UserKeyword> {

    void deleteByUserId(long userId);

    void deleteByUserIdIn(Collection<Long> userIds);

    UserKeyword findByUserIdAndKeyword(
        long userId,
        String keyword
    );

    @EzyQuery(
        "SELECT e.userId FROM UserKeyword e " +
        "WHERE e.keyword IN ?0 " +
        "AND e.userId NOT IN ?1 " +
        "ORDER BY e.priority DESC, e.id DESC"
    )
    List<IdResult> findUserIdsByKeywordInAndUserIdNotInOrderByPriorityDescIdDesc(
        Collection<String> keywords,
        Collection<Long> exclusiveUserIds,
        Next next
    );
}
