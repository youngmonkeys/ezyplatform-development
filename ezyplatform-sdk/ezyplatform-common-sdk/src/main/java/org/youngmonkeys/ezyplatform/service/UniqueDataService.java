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

import com.tvd12.ezyfox.util.EzyEntry;
import org.youngmonkeys.ezyplatform.model.UniqueDataKeyValueModel;
import org.youngmonkeys.ezyplatform.model.UniqueDataModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface UniqueDataService {

    void saveDataMeta(UniqueDataModel model);

    void saveDataMetaList(List<UniqueDataModel> models);

    void saveDataMetaList(
        String dataType,
        long dataId,
        List<UniqueDataKeyValueModel> models
    );

    void deleteByDataTypeAndUniqueKey(
        String dataType,
        String uniqueKey
    );

    void deleteByDataTypeAndUniqueKeys(
        String dataType,
        Collection<String> uniqueKeys
    );

    UniqueDataModel getUniqueDataByDataTypeAndDataIdAndUniqueKey(
        String dataType,
        long dataId,
        String uniqueKey
    );

    long getDataIdByDataTypeAndUniqueKeyAndTextValue(
        String dataType,
        String uniqueKey,
        String textValue
    );

    Map<Long, UniqueDataModel> getUniqueDataMapByDataTypeAndDataIdsAndUniqueKey(
        String dataType,
        Collection<Long> dataIds,
        String uniqueKey
    );

    Map<String, UniqueDataModel> getUniqueDataMapByDataTypeAndDataIdAndUniqueKeys(
        String dataType,
        long dataId,
        Collection<String> uniqueKeys
    );

    Map<Long, Map<String, UniqueDataModel>> getUniqueDataMapsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    );

    Map<Long, Map<String, UniqueDataModel>> getUniqueDataMapsByDataTypeAndDataIdsAndUniqueKeys(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> uniqueKeys
    );

    default <T> T getUniqueDataValueByDataTypeAndDataIdAndUniqueKey(
        String dataType,
        long dataId,
        String uniqueKey,
        Function<UniqueDataModel, T> converter
    ) {
        UniqueDataModel model = getUniqueDataByDataTypeAndDataIdAndUniqueKey(
            dataType,
            dataId,
            uniqueKey
        );
        return model != null ? converter.apply(model) : null;
    }

    default <T> T getUniqueDataValueByDataTypeAndDataIdAndUniqueKey(
        String dataType,
        long dataId,
        String uniqueKey,
        Function<UniqueDataModel, T> converter,
        T defaultValue
    ) {
        T value = getUniqueDataValueByDataTypeAndDataIdAndUniqueKey(
            dataType,
            dataId,
            uniqueKey,
            converter
        );
        return value != null ? value : defaultValue;
    }

    default <T> Map<Long, T> getUniqueDataValueMapByDataTypeAndDataIdsAndUniqueKey(
        String dataType,
        Collection<Long> dataIds,
        String uniqueKey,
        Function<UniqueDataModel, T> converter
    ) {
        return getUniqueDataMapByDataTypeAndDataIdsAndUniqueKey(
            dataType,
            dataIds,
            uniqueKey
        )
            .entrySet()
            .stream()
            .map(it ->
                EzyEntry.of(
                    it.getKey(),
                    converter.apply(it.getValue())
                )
            )
            .filter(it -> it.getValue() != null)
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    EzyEntry::getValue
                )
            );
    }

    default <T> Map<String, T> getUniqueDataValueMapByDataTypeAndDataIdAndUniqueKeys(
        String dataType,
        long dataId,
        Collection<String> uniqueKeys,
        Function<UniqueDataModel, T> converter
    ) {
        return getUniqueDataMapByDataTypeAndDataIdAndUniqueKeys(
            dataType,
            dataId,
            uniqueKeys
        )
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> converter.apply(e.getValue())
                )
            );
    }

    default <T> Map<Long, Map<String, T>> getUniqueDataValueMapsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds,
        Function<UniqueDataModel, T> converter
    ) {
        return getUniqueDataMapsByDataTypeAndDataIds(dataType, dataIds)
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e
                        .getValue()
                        .entrySet()
                        .stream()
                        .map(it ->
                            EzyEntry.of(
                                it.getKey(),
                                converter.apply(it.getValue())
                            )
                        )
                        .filter(it -> it.getValue() != null)
                        .collect(
                            Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                            )
                        )
                )
            );
    }

    default <T> Map<Long, Map<String, T>> getUniqueDataValueMapsByDataTypeAndDataIdsAndUniqueKeys(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> uniqueKeys,
        Function<UniqueDataModel, T> converter
    ) {
        return getUniqueDataMapsByDataTypeAndDataIdsAndUniqueKeys(
            dataType,
            dataIds,
            uniqueKeys
        )
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e
                        .getValue()
                        .entrySet()
                        .stream()
                        .map(it ->
                            EzyEntry.of(
                                it.getKey(),
                                converter.apply(it.getValue())
                            )
                        )
                        .filter(it -> it.getValue() != null)
                        .collect(
                            Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                            )
                        )
                )
            );
    }
}
