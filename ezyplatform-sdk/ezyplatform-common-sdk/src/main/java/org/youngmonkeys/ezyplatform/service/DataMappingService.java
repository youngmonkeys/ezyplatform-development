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

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DataMappingService {

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

    DataMappingModel getMappingToDataByNameAndFromDataId(
        String mappingName,
        long fromDataId
    );

    DataMappingModel getMappingFromDataByNameAndToDataId(
        String mappingName,
        long toDataId
    );

    Map<Long, Long> getMappingToDataIdByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    Map<Long, List<Long>> getMappingToDataIdsByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    );

    Map<Long, Long> getMappingFromDataIdByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    );

    Map<Long, List<Long>> getMappingFromDataIdsByNameAndToDataIds(
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
