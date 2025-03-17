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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.UniqueData;
import org.youngmonkeys.ezyplatform.entity.UniqueDataId;
import org.youngmonkeys.ezyplatform.model.UniqueDataModel;
import org.youngmonkeys.ezyplatform.repo.UniqueDataRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@SuppressWarnings("LineLength")
public class DefaultUniqueDataService implements UniqueDataService {

    private final UniqueDataRepository uniqueDataRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;


    @Override
    public void saveDataMeta(UniqueDataModel model) {
        uniqueDataRepository.save(
            modelToEntityConverter.toEntity(model)
        );
    }

    @Override
    public UniqueDataModel getUniqueDataByDataTypeAndDataIdAndUniqueKey(
        String dataType,
        long dataId,
        String uniqueKey
    ) {
        return entityToModelConverter.toModel(
            uniqueDataRepository.findById(
                new UniqueDataId(
                    dataType,
                    dataId,
                    uniqueKey
                )
            )
        );
    }

    @Override
    public Map<Long, UniqueDataModel> getUniqueDataMapByDataTypeAndDataIdsAndUniqueKey(
        String dataType,
        Collection<Long> dataIds,
        String uniqueKey
    ) {
        if (dataIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return uniqueDataRepository
            .findByDataTypeAndDataIdInAndUniqueKey(
                dataType,
                dataIds,
                uniqueKey
            )
            .stream()
            .collect(
                Collectors.toMap(
                    UniqueData::getDataId,
                    entityToModelConverter::toModel
                )
            );
    }

    @Override
    public Map<Long, Map<String, UniqueDataModel>> getUniqueDataMapsByDataTypeAndDataIdsAndUniqueKeys(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> uniqueKeys
    ) {
        if (dataIds.isEmpty() || uniqueKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return uniqueDataRepository
            .findByDataTypeAndDataIdInAndUniqueKeyIn(
                dataType,
                dataIds,
                uniqueKeys
            )
            .stream()
            .collect(
                Collectors.groupingBy(
                    UniqueData::getDataId,
                    Collectors.toMap(
                        UniqueData::getUniqueKey,
                        entityToModelConverter::toModel
                    )
                )
            );
    }
}
