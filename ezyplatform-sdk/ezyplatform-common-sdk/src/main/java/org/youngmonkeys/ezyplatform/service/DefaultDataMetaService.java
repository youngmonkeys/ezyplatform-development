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
import org.youngmonkeys.ezyplatform.entity.DataMeta;
import org.youngmonkeys.ezyplatform.repo.DataMetaRepository;
import org.youngmonkeys.ezyplatform.repo.DataMetaTransactionalRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultDataMetaService implements DataMetaService {

    private final DataMetaRepository dataMetaRepository;
    private final DataMetaTransactionalRepository dataMetaTransactionalRepository;

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
        String metaValue
    ) {
        dataMetaTransactionalRepository.saveDataMetaUniqueKey(
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
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaKey,
                    DataMeta::getMetaValue
                )
            );
    }

    @Override
    public Map<String, Long> getDataIdMapByMetaValues(
        String dataType,
        String metaKey,
        Collection<String> metaValues
    ) {
        return dataMetaRepository.findByDataTypeAndMetaKeyAndMetaValueIn(
            dataType,
            metaKey,
            metaValues
        )
            .stream()
            .collect(
                Collectors.toMap(
                    DataMeta::getMetaValue,
                    DataMeta::getDataId
                )
            );
    }

    @Override
    public Map<Long, String> getDataMetaValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    ) {
        return dataMetaRepository.findByDataTypeAndDataIdInAndMetaKey(
                dataType,
                dataIds,
                metaKey
            )
            .stream()
            .collect(
                Collectors.toMap(
                    DataMeta::getDataId,
                    DataMeta::getMetaValue
                )
            );
    }
}
