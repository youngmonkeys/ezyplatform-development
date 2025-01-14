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
import org.youngmonkeys.ezyplatform.model.DataMappingModel;
import org.youngmonkeys.ezyplatform.model.SaveDataMappingModel;
import org.youngmonkeys.ezyplatform.repo.DataMappingRepository;
import org.youngmonkeys.ezyplatform.result.DataFromToIdResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultDataMappingService implements DataMappingService {

    private final DataMappingRepository dataMappingRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void saveDataMapping(
        String mappingName,
        long fromDataId,
        long toDataId
    ) {
        dataMappingRepository.save(
            modelToEntityConverter.toEntity(
                SaveDataMappingModel.builder()
                    .mappingName(mappingName)
                    .fromDataId(fromDataId)
                    .toDataId(toDataId)
                    .build()
            )
        );
    }

    @Override
    public void saveDataMappings(
        String mappingName,
        long fromDataId,
        Collection<Long> toDataIds
    ) {
        dataMappingRepository.save(
            modelToEntityConverter.toEntities(
                mappingName,
                fromDataId,
                toDataIds
            )
        );
    }

    @Override
    public void saveDataMappings(
        String mappingName,
        Collection<Long> fromDataIds,
        long toDataId
    ) {
        dataMappingRepository.save(
            modelToEntityConverter.toEntities(
                mappingName,
                fromDataIds,
                toDataId
            )
        );
    }

    @Override
    public void saveDataMappings(
        String mappingName,
        Map<Long, Long> toDataIdByFromDataId
    ) {
        dataMappingRepository.save(
            modelToEntityConverter.toEntities(
                mappingName,
                toDataIdByFromDataId
            )
        );
    }

    @Override
    public DataMappingModel getMappingToDataByNameAndFromDataId(
        String mappingName,
        long fromDataId
    ) {
        return entityToModelConverter.toModel(
            dataMappingRepository
                .findByMappingNameAndFromDataIdOrderByMappedAtDesc(
                    mappingName,
                    fromDataId
                )
        );
    }

    @Override
    public DataMappingModel getMappingFromDataByNameAndToDataId(
        String mappingName,
        long toDataId
    ) {
        return entityToModelConverter.toModel(
            dataMappingRepository
                .findByMappingNameAndToDataIdOrderByMappedAtDesc(
                    mappingName,
                    toDataId
                )
        );
    }

    @Override
    public Map<Long, Long> getMappingToDataIdByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        return dataMappingRepository
            .findDataFromToIdsByMappingNameAndFromDataIdInOrderByMappedAtDesc(
                mappingName,
                fromDataIds
            )
            .stream()
            .collect(
                Collectors.toMap(
                    DataFromToIdResult::getFromDataId,
                    DataFromToIdResult::getToDataId,
                    (o, n) -> o
                )
            );
    }

    @Override
    public Map<Long, List<Long>> getMappingToDataIdsByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        return dataMappingRepository
            .findDataFromToIdsByMappingNameAndFromDataIdInOrderByMappedAtDesc(
                mappingName,
                fromDataIds
            )
            .stream()
            .collect(
                Collectors.groupingBy(
                    DataFromToIdResult::getFromDataId,
                    Collectors.mapping(
                        DataFromToIdResult::getToDataId,
                        Collectors.toList()
                    )
                )
            );
    }

    @Override
    public Map<Long, Long> getMappingFromDataIdByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        return dataMappingRepository
            .findDataFromToIdsByMappingNameAndToDataIdInOrderByMappedAtDesc(
                mappingName,
                toDataIds
            )
            .stream()
            .collect(
                Collectors.toMap(
                    DataFromToIdResult::getToDataId,
                    DataFromToIdResult::getFromDataId,
                    (o, n) -> o
                )
            );
    }

    @Override
    public Map<Long, List<Long>> getMappingFromDataIdsByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        return dataMappingRepository
            .findDataFromToIdsByMappingNameAndToDataIdInOrderByMappedAtDesc(
                mappingName,
                toDataIds
            )
            .stream()
            .collect(
                Collectors.groupingBy(
                    DataFromToIdResult::getToDataId,
                    Collectors.mapping(
                        DataFromToIdResult::getFromDataId,
                        Collectors.toList()
                    )
                )
            );
    }

    @Override
    public List<DataMappingModel> getMappingDataListByMappingNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        return dataMappingRepository
            .findByMappingNameAndFromDataIdInOrderByMappedAtDesc(
                mappingName,
                fromDataIds
            )
            .stream()
            .map(entityToModelConverter::toModel)
            .collect(Collectors.toList());
    }

    @Override
    public List<DataMappingModel> getMappingDataListByMappingNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        return dataMappingRepository
            .findByMappingNameAndToDataIdInOrderByMappedAtDesc(
                mappingName,
                toDataIds
            )
            .stream()
            .map(entityToModelConverter::toModel)
            .collect(Collectors.toList());
    }
}
