/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.model.DataMappingModel;
import org.youngmonkeys.ezyplatform.model.SaveDataMappingModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataMappingService {

    void saveDataMapping(
        SaveDataMappingModel model
    );

    void saveDataMapping(
        String mappingName,
        long fromDataId,
        long toDataId
    );

    void saveDataMappings(
        String mappingName,
        long fromDataId,
        Collection<Long> toDataIds
    );

    void saveDataMappings(
        String mappingName,
        Collection<Long> fromDataIds,
        long toDataId
    );

    void saveDataMappings(
        String mappingName,
        Map<Long, Long> toDataIdByFromDataId
    );

    void removeDataMapping(
        String mappingName,
        long fromDataId,
        long toDataId
    );

    void removeDataMappingByFromDataId(
        String mappingName,
        long fromDataId
    );

    void removeDataMappingByToDataId(
        String mappingName,
        long toDataId
    );

    void removeDataMappings(
        String mappingName,
        long fromDataId,
        Collection<Long> toDataIds
    );

    void removeDataMappings(
        String mappingName,
        Collection<Long> fromDataIds,
        long toDataId
    );

    void removeDataMappings(
        String mappingName,
        Map<Long, Long> toDataIdByFromDataId
    );

    void removeDataMappingsByFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    void removeDataMappingsByToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );

    DataMappingModel getMappingToDataByNameAndFromDataId(
        String mappingName,
        long fromDataId
    );

    DataMappingModel getMappingFromDataByNameAndToDataId(
        String mappingName,
        long toDataId
    );

    Set<Long> getMappingToDataIdsByNameAndFromDataId(
        String mappingName,
        long fromDataId
    );

    Set<Long> getMappingToDataIdsByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    Map<Long, Long> getMappingToDataIdMapByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    Map<Long, List<Long>> getMappingToDataIdsMapByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    Set<Long> getMappingFromDataIdsByNameAndToDataId(
        String mappingName,
        long toDataId
    );

    Set<Long> getMappingFromDataIdsByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );

    Map<Long, Long> getMappingFromDataIdMapByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );

    Map<Long, List<Long>> getMappingFromDataIdsMapByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );

    List<DataMappingModel> getMappingDataListByMappingNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    List<DataMappingModel> getMappingDataListByMappingNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );
}
