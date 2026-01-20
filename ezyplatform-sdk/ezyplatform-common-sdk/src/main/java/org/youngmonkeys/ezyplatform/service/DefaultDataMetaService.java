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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyfox.util.Next;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.DataMeta;
import org.youngmonkeys.ezyplatform.model.DataMetaModel;
import org.youngmonkeys.ezyplatform.repo.DataMetaRepository;
import org.youngmonkeys.ezyplatform.repo.DataMetaTransactionalRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

@SuppressWarnings("MethodCount")
@AllArgsConstructor
public class DefaultDataMetaService implements DataMetaService {

    private final DataMetaRepository dataMetaRepository;
    private final DataMetaTransactionalRepository dataMetaTransactionalRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    @Override
    public void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        DataMeta entity = new DataMeta();
        entity.setDataType(dataType);
        entity.setDataId(dataId);
        entity.setMetaKey(metaKey);
        entity.setMetaValue(metaValue);
        entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
        dataMetaRepository.save(entity);
    }

    @Override
    public void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        List<String> metaValues
    ) {
        List<DataMeta> entities = newArrayList(
            metaValues,
            metaValue -> {
                DataMeta entity = new DataMeta();
                entity.setDataType(dataType);
                entity.setDataId(dataId);
                entity.setMetaKey(metaKey);
                entity.setMetaValue(metaValue);
                entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
                return entity;
            }
        );
        dataMetaRepository.save(entities);
    }

    @Override
    public void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue,
        String metaTextValue
    ) {
        dataMetaTransactionalRepository.saveDataMetaUniqueKey(
            dataType,
            dataId,
            metaKey,
            metaValue,
            metaTextValue
        );
    }

    @Override
    public void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        dataMetaTransactionalRepository.saveDataMetaUniqueKeyValue(
            dataType,
            dataId,
            metaKey,
            metaValue
        );
    }

    @Override
    public BigDecimal increaseDataMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        BigDecimal value
    ) {
        return dataMetaTransactionalRepository.increaseMetaValue(
            dataType,
            dataId,
            metaKey,
            value
        );
    }

    @Override
    public void deleteDataMetaById(long id) {
        dataMetaRepository.delete(id);
    }

    @Override
    public void deleteDataMetaByDataTypeAndDataId(
        String dataType,
        long dataId
    ) {
        dataMetaRepository.deleteByDataTypeAndDataId(
            dataType,
            dataId
        );
    }

    @Override
    public void deleteDataMetaByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    ) {
        if (!dataIds.isEmpty()) {
            dataMetaRepository.deleteByDataTypeAndDataIdIn(
                dataType,
                dataIds
            );
        }
    }

    @Override
    public void deleteByDataTypeAndMetaKey(
        String dataType,
        String metaKey
    ) {
        dataMetaRepository.deleteByDataTypeAndMetaKey(
            dataType,
            metaKey
        );
    }

    @Override
    public void deleteByDataTypeMetaKey(
        String dataType,
        String metaKey
    ) {
        dataMetaRepository.deleteByDataTypeAndMetaKey(
            dataType,
            metaKey
        );
    }

    @Override
    public void deleteByDataTypeAndDataIdInAndMetaKeyIn(
        String dataType,
        Collection<Long> productIds,
        Collection<String> metaKeys
    ) {
        if (!productIds.isEmpty() && !metaKeys.isEmpty()) {
            dataMetaRepository.deleteByDataTypeAndDataIdInAndMetaKeyIn(
                dataType,
                productIds,
                metaKeys
            );
        }
    }

    @Override
    public void deleteByDataTypeAndDataIdAndMetaKeyAndMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        dataMetaRepository.deleteByDataTypeAndDataIdAndMetaKeyAndMetaValue(
            dataType,
            dataId,
            metaKey,
            metaValue
        );
    }

    @Override
    public void deleteByDataTypeAndDataIdsAndMetaKeyAndMetaValue(
        String dataType,
        Collection<Long> dataIds,
        String metaKey,
        String metaValue
    ) {
        if (dataIds.isEmpty()) {
            return;
        }
        dataMetaRepository.deleteByDataTypeAndDataIdInAndMetaKeyAndMetaValue(
            dataType,
            dataIds,
            metaKey,
            metaValue
        );
    }

    @Override
    public void deleteByDataTypeAndDataIdAndMetaKeyAndMetaNumberValue(
        String dataType,
        long dataId,
        String metaKey,
        BigInteger metaNumberValue
    ) {
        dataMetaRepository.deleteByDataTypeAndDataIdAndMetaKeyAndMetaNumberValue(
            dataType,
            dataId,
            metaKey,
            metaNumberValue
        );
    }

    @Override
    public void deleteByDataTypeAndDataIdsAndMetaKeyAndMetaNumberValue(
        String dataType,
        Collection<Long> dataIds,
        String metaKey,
        BigInteger metaNumberValue
    ) {
        if (dataIds.isEmpty()) {
            return;
        }
        dataMetaRepository.deleteByDataTypeAndDataIdInAndMetaKeyAndMetaNumberValue(
            dataType,
            dataIds,
            metaKey,
            metaNumberValue
        );
    }

    @Override
    public boolean containsDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        return dataMetaRepository.findByDataTypeAndDataIdAndMetaKeyAndMetaValue(
            dataType,
            dataId,
            metaKey,
            metaValue
        ).isPresent();
    }

    @Override
    public long getDataIdByMeta(
        String dataType,
        String metaKey,
        String metaValue
    ) {
        return dataMetaRepository.findByDataTypeAndMetaKeyAndMetaValue(
            dataType,
            metaKey,
            metaValue
        )
            .map(DataMeta::getDataId)
            .orElse(0L);
    }

    @Override
    public String getMetaValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        return dataMetaRepository.findByDataTypeAndDataIdAndMetaKey(
            dataType,
            dataId,
            metaKey
        )
            .map(DataMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        return dataMetaRepository.findByDataTypeDataIdAndMetaKeyOrderByIdDesc(
            dataType,
            dataId,
            metaKey
        )
            .map(DataMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getMetaTextValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        return dataMetaRepository.findByDataTypeAndDataIdAndMetaKey(
                dataType,
                dataId,
                metaKey
            )
            .map(DataMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaTextValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        return dataMetaRepository.findByDataTypeDataIdAndMetaKeyOrderByIdDesc(
                dataType,
                dataId,
                metaKey
            )
            .map(DataMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public List<String> getMetaValuesByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey,
        int limit
    ) {
        return newArrayList(
            dataMetaRepository.findByDataTypeAndDataIdAndMetaKey(
                dataType,
                dataId,
                metaKey,
                Next.limit(limit)
            ),
            DataMeta::getMetaValue
        );
    }

    @Override
    public Map<String, String> getDataMetaValues(
        String dataType,
        long dataId
    ) {
        return dataMetaRepository.findByDataTypeAndDataId(
            dataType,
            dataId
        )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaKey,
                    DataMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, String> getDataMetaTextValues(
        String dataType,
        long dataId
    ) {
        return dataMetaRepository.findByDataTypeAndDataId(
                dataType,
                dataId
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaKey,
                    DataMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, Map<String, DataMetaModel>> getDataMetaValueMapsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    ) {
        if (dataIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository
            .findByDataTypeAndDataIdIn(dataType, dataIds)
            .stream()
            .collect(
                Collectors.groupingBy(
                    DataMeta::getDataId,
                    Collectors.toMap(
                        DataMeta::getMetaKey,
                        entityToModelConverter::toModel,
                        (o, n) -> n
                    )
                )
            );
    }

    @Override
    public Map<String, Long> getDataIdMapByMetaValues(
        String dataType,
        String metaKey,
        Collection<String> metaValues
    ) {
        if (metaValues.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndMetaKeyAndMetaValueIn(
            dataType,
            metaKey,
            metaValues
        )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaValue,
                    DataMeta::getDataId,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getDataMetaValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    ) {
        if (dataIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndDataIdInAndMetaKey(
                dataType,
                dataIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getDataId,
                    DataMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }
    
    public Map<String, String> getDataMetaTextValueMapByDataTypeAndDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    ) {
        if (metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndDataIdAndMetaKeyIn(
                dataType,
                dataId,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaKey,
                    DataMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, String> getDataMetaValueMapByDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    ) {
        if (metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndDataIdAndMetaKeyIn(
                dataType,
                dataId,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaKey,
                    DataMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getDataMetaTextValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    ) {
        if (dataIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndDataIdInAndMetaKey(
                dataType,
                dataIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    DataMeta::getDataId,
                    DataMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public List<DataMetaModel> getMetaListByDataTypeAndDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    ) {
        return newArrayList(
            dataMetaRepository.findByDataTypeAndDataIdAndMetaKeyIn(
                dataType,
                dataId,
                metaKeys
            ),
            entityToModelConverter::toModel
        );
    }

    @Override
    public Map<Long, Map<String, String>> getDataMetaValueMapsByDataTypeAndDataIdsAndMetaKeys(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> metaKeys
    ) {
        if (dataIds.isEmpty() || metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataMetaRepository.findByDataTypeAndDataIdInAndMetaKeyIn(
                dataType,
                dataIds,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.groupingBy(
                    DataMeta::getDataId,
                    Collectors.toMap(
                        DataMeta::getMetaKey,
                        DataMeta::getMetaValue,
                        (o, n) -> n
                    )
                )
            );
    }
}
