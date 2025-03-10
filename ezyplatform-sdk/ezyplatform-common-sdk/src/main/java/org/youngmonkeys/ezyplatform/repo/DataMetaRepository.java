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
import org.youngmonkeys.ezyplatform.entity.DataMeta;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DataMetaRepository
    extends EzyDatabaseRepository<Long, DataMeta> {

    List<DataMeta> findByDataTypeAndDataId(
        String dataType,
        long dataId
    );

    Optional<DataMeta> findByDataTypeAndMetaKeyAndMetaValue(
        String dataType,
        String metaKey,
        String metaValue
    );

    Optional<DataMeta> findByDataTypeAndDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    );

    List<DataMeta> findByDataTypeAndDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey,
        Next next
    );

    @EzyQuery(
        "SELECT e FROM DataMeta e " +
            "WHERE e.dataType = ?0 AND e.dataId = ?1 AND e.metaKey = ?2 " +
            "ORDER BY e.id DESC"
    )
    Optional<DataMeta> findByDataTypeDataIdAndMetaKeyOrderByIdDesc(
        String dataType,
        long dataId,
        String metaKey
    );

    Optional<DataMeta> findByDataTypeAndDataIdAndMetaKeyAndMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    );

    List<DataMeta> findByDataTypeAndMetaKeyAndMetaValueIn(
        String dataType,
        String metaKey,
        Collection<String> metaValues
    );

    List<DataMeta> findByDataTypeAndDataIdInAndMetaKey(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    );

    List<DataMeta> findByDataTypeAndDataIdAndMetaKeyIn(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    );
}
