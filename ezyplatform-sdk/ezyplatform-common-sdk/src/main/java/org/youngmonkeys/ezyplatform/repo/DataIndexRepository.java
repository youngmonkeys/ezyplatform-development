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
import org.youngmonkeys.ezyplatform.entity.DataIndex;
import org.youngmonkeys.ezyplatform.result.IdResult;

import java.util.Collection;
import java.util.List;

public interface DataIndexRepository
    extends EzyDatabaseRepository<Long, DataIndex> {

    void deleteByDataTypeAndDataId(
        String dataType,
        long dataId
    );

    void deleteByDataTypeAndDataIdIn(
        String dataType,
        Collection<Long> dataIds
    );

    DataIndex findByDataTypeAndDataIdAndKeyword(
        String dataType,
        long dataId,
        String keyword
    );

    @EzyQuery(
        "SELECT e.dataId FROM DataIndex e " +
        "WHERE e.dataType = ?0 " +
        "AND e.keyword IN ?1 " +
        "AND e.dataId NOT IN ?2 " +
        "ORDER BY e.priority DESC, e.id DESC"
    )
    List<IdResult> findDataIdsByDataTypeAndKeywordInAndDataIdNotInOrderByPriorityDescIdDesc(
        String dataType,
        Collection<String> keywords,
        Collection<Long> exclusiveDataIds,
        Next next
    );
}
