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
import org.youngmonkeys.ezyplatform.entity.DataMapping;
import org.youngmonkeys.ezyplatform.entity.DataMappingId;
import org.youngmonkeys.ezyplatform.result.DataFromToIdResult;

import java.util.Collection;
import java.util.List;

public interface DataMappingRepository
    extends EzyDatabaseRepository<DataMappingId, DataMapping> {

    @EzyQuery(
        "SELECT e FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.fromDataId = ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    DataMapping findByMappingNameAndFromDataIdOrderByMappedAtDesc(
        String mappingName,
        long fromDataId
    );

    @EzyQuery(
        "SELECT e FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.toDataId = ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    DataMapping findByMappingNameAndToDataIdOrderByMappedAtDesc(
        String mappingName,
        long toDataId
    );

    @EzyQuery(
        "SELECT e FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.fromDataId IN ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    List<DataMapping> findByMappingNameAndFromDataIdInOrderByMappedAtDesc(
        String mappingName,
        Collection<Long> fromDataIds
    );

    @EzyQuery(
        "SELECT e FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.toDataId IN ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    List<DataMapping> findByMappingNameAndToDataIdInOrderByMappedAtDesc(
        String mappingName,
        Collection<Long> toDataIds
    );

    @EzyQuery(
        "SELECT e.fromDataId, e.toDataId FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.fromDataId IN ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    List<DataFromToIdResult> findDataFromToIdsByMappingNameAndFromDataIdInOrderByMappedAtDesc(
        String mappingName,
        Collection<Long> fromDataIds
    );

    @EzyQuery(
        "SELECT e.fromDataId, e.toDataId FROM DataMapping e " +
            "WHERE e.mappingName = ?0 " +
            "AND e.toDataId IN ?1 " +
            "ORDER BY e.mappedAt DESC"
    )
    List<DataFromToIdResult> findDataFromToIdsByMappingNameAndToDataIdInOrderByMappedAtDesc(
        String mappingName,
        Collection<Long> toDataIds
    );
}
