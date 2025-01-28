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
import org.youngmonkeys.ezyplatform.entity.DataMappingId;
import org.youngmonkeys.ezyplatform.model.DataMappingModel;
import org.youngmonkeys.ezyplatform.model.SaveDataMappingModel;
import org.youngmonkeys.ezyplatform.repo.DataMappingRepository;
import org.youngmonkeys.ezyplatform.result.DataFromToIdResult;

import java.util.*;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzySets.newHashSet;

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
    public void removeDataMapping(
        String mappingName,
        long fromDataId,
        long toDataId
    ) {
        dataMappingRepository.delete(
            new DataMappingId(
                mappingName,
                fromDataId,
                toDataId
            )
        );
    }

    @Override
    public void removeDataMappingByFromDataId(
        String mappingName,
        long fromDataId
    ) {
        dataMappingRepository.deleteByMappingNameAndFromDataId(
            mappingName,
            fromDataId
        );
    }

    @Override
    public void removeDataMappingByToDataId(
        String mappingName,
        long toDataId
    ) {
        dataMappingRepository.deleteByMappingNameAndToDataId(
            mappingName,
            toDataId
        );
    }

    @Override
    public void removeDataMappings(
        String mappingName,
        long fromDataId,
        Collection<Long> toDataIds
    ) {
        if (toDataIds.isEmpty()) {
            return;
        }
        dataMappingRepository.deleteByIds(
            newArrayList(
                toDataIds,
                toDataId -> new DataMappingId(
                    mappingName,
                    fromDataId,
                    toDataId
                )
            )
        );
    }

    @Override
    public void removeDataMappings(
        String mappingName,
        Collection<Long> fromDataIds,
        long toDataId
    ) {
        if (fromDataIds.isEmpty()) {
            return;
        }
        dataMappingRepository.deleteByIds(
            newArrayList(
                fromDataIds,
                fromDataId -> new DataMappingId(
                    mappingName,
                    fromDataId,
                    toDataId
                )
            )
        );
    }

    @Override
    public void removeDataMappings(
        String mappingName,
        Map<Long, Long> toDataIdByFromDataId
    ) {
        if (toDataIdByFromDataId.isEmpty()) {
            return;
        }
        dataMappingRepository.deleteByIds(
            newArrayList(
                toDataIdByFromDataId.entrySet(),
                e -> new DataMappingId(
                    mappingName,
                    e.getKey(),
                    e.getValue()
                )
            )
        );
    }

    @Override
    public void removeDataMappingsByFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        if (fromDataIds.isEmpty()) {
            return;
        }
        dataMappingRepository.deleteByMappingNameAndFromDataIdIn(
            mappingName,
            fromDataIds
        );
    }

    @Override
    public void removeDataMappingsByToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        if (toDataIds.isEmpty()) {
            return;
        }
        dataMappingRepository.deleteByMappingNameAndToDataIdIn(
            mappingName,
            toDataIds
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

    public Set<Long> getMappingToDataIdsByNameAndFromDataId(
        String mappingName,
        long fromDataId
    ) {
        return newHashSet(
            dataMappingRepository
                .findDataFromToIdsByMappingNameAndFromDataIdOrderByMappedAtDesc(
                    mappingName,
                    fromDataId
                ),
            DataFromToIdResult::getToDataId
        );
    }

    @Override
    public Set<Long> getMappingToDataIdsByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        if (fromDataIds.isEmpty()) {
            return Collections.emptySet();
        }
        return newHashSet(
            dataMappingRepository
                .findDataFromToIdsByMappingNameAndFromDataIdInOrderByMappedAtDesc(
                    mappingName,
                    fromDataIds
                ),
            DataFromToIdResult::getToDataId
        );
    }

    @Override
    public Map<Long, Long> getMappingToDataIdMapByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        if (fromDataIds.isEmpty()) {
            return Collections.emptyMap();
        }
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
    public Map<Long, List<Long>> getMappingToDataIdsMapByNameAndFromDataIds(
        String mappingName,
        Collection<Long> fromDataIds
    ) {
        if (fromDataIds.isEmpty()) {
            return Collections.emptyMap();
        }
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

    public Set<Long> getMappingFromDataIdsByNameAndToDataId(
        String mappingName,
        long toDataId
    ) {
        return newHashSet(
            dataMappingRepository
                .findDataFromToIdsByMappingNameAndToDataIdOrderByMappedAtDesc(
                    mappingName,
                    toDataId
                ),
            DataFromToIdResult::getFromDataId
        );
    }

    @Override
    public Set<Long> getMappingFromDataIdsByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        if (toDataIds.isEmpty()) {
            return Collections.emptySet();
        }
        return newHashSet(
            dataMappingRepository
                .findDataFromToIdsByMappingNameAndToDataIdInOrderByMappedAtDesc(
                    mappingName,
                    toDataIds
                ),
            DataFromToIdResult::getFromDataId
        );
    }

    @Override
    public Map<Long, Long> getMappingFromDataIdMapByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        if (toDataIds.isEmpty()) {
            return Collections.emptyMap();
        }
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
    public Map<Long, List<Long>> getMappingFromDataIdsMapByNameAndToDataIds(
        String mappingName,
        Collection<Long> toDataIds
    ) {
        if (toDataIds.isEmpty()) {
            return Collections.emptyMap();
        }
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
        if (fromDataIds.isEmpty()) {
            return Collections.emptyList();
        }
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
        if (toDataIds.isEmpty()) {
            return Collections.emptyList();
        }
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
