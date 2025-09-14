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

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyEntry;
import com.tvd12.reflections.util.Predicates;
import org.youngmonkeys.ezyplatform.model.DataMetaModel;
import org.youngmonkeys.ezyplatform.util.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@SuppressWarnings("MethodCount")
public interface DataMetaService {

    void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    );

    default void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        Object metaValue
    ) {
        saveDataMeta(
            dataType,
            dataId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        List<String> metaValues
    );

    default void saveDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        saveDataMeta(
            dataType,
            dataId,
            metaKey,
            (List<String>) newArrayList(
                metaValues,
                Strings::from
            )
        );
    }

    default void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        Object metaValue
    ) {
        saveDataMetaIfAbsent(
            dataType,
            dataId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    );

    default void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        List<String> metaValues
    ) {
        metaValues
            .parallelStream()
            .filter(Objects::nonNull)
            .forEach(metaValue ->
                saveDataMetaIfAbsent(
                    dataType,
                    dataId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        metaValues
            .parallelStream()
            .filter(Objects::nonNull)
            .forEach(metaValue ->
                saveDataMetaIfAbsent(
                    dataType,
                    dataId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        Object metaValue
    ) {
        saveDataMetaUniqueKey(
            dataType,
            dataId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    default void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        saveDataMetaUniqueKey(
            dataType,
            dataId,
            metaKey,
            metaValue,
            null
        );
    }

    void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue,
        String metaTextValue
    );

    default void saveDataMetaUniqueKeys(
        String dataType,
        long dataId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveDataMetaUniqueKey(
                    dataType,
                    dataId,
                    e.getKey(),
                    e.getValue()
                )
            );
    }

    default void saveDataMetaTextValueUniqueKeys(
        String dataType,
        long dataId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveDataMetaUniqueKey(
                    dataType,
                    dataId,
                    e.getKey(),
                    EMPTY_STRING,
                    Strings.from(e.getValue())
                )
            );
    }

    BigDecimal increaseDataMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        BigDecimal value
    );

    default BigInteger increaseDataMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        BigInteger value
    ) {
        return increaseDataMetaValue(
            dataType,
            dataId,
            metaKey,
            new BigDecimal(value)
        ).toBigInteger();
    }

    void deleteDataMetaById(long id);

    void deleteDataMetaByDataTypeAndDataId(
        String dataType,
        long dataId
    );

    void deleteDataMetaByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    );

    boolean containsDataMeta(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    );

    long getDataIdByMeta(
        String dataType,
        String metaKey,
        String metaValue
    );

    default long getDataIdByMeta(
        String dataType,
        String metaKey,
        Object metaValue
    ) {
        return getDataIdByMeta(
            dataType,
            metaKey,
            Strings.from(metaValue)
        );
    }

    String getMetaValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    );

    String getLatestMetaValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    );

    default String getMetaValueByDataIdAndMetaKeyOrDefault(
        String dataType,
        long dataId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaValueByDataIdAndMetaKey(
            dataType,
            dataId,
            metaKey
        );
        return value != null ? value : defaultValue;
    }

    String getMetaTextValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    );

    String getLatestMetaTextValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    );

    default String getMetaTextValueByDataIdAndMetaKeyOrDefault(
        String dataType,
        long dataId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaTextValueByDataIdAndMetaKey(
            dataType,
            dataId,
            metaKey
        );
        return value != null ? value : defaultValue;
    }

    default BigDecimal getMetaDecimalValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        String value = getMetaValueByDataIdAndMetaKey(
            dataType,
            dataId,
            metaKey
        );
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    default BigInteger getMetaIntegerValueByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey
    ) {
        String value = getMetaValueByDataIdAndMetaKey(
            dataType,
            dataId,
            metaKey
        );
        return value != null ? new BigInteger(value) : BigInteger.ZERO;
    }

    List<String> getMetaValuesByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey,
        int limit
    );

    default <T> List<T> getMetaValuesByDataIdAndMetaKey(
        String dataType,
        long dataId,
        String metaKey,
        int limit,
        Function<String, T> valueConverter
    ) {
        return newArrayList(
            getMetaValuesByDataIdAndMetaKey(
                dataType,
                dataId,
                metaKey,
                limit
            ),
            valueConverter
        );
    }

    Map<String, String> getDataMetaValues(
        String dataType,
        long dataId
    );

    Map<String, String> getDataMetaTextValues(
        String dataType,
        long dataId
    );

    Map<Long, Map<String, DataMetaModel>> getDataMetaValueMapsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    );

    default  <T> Map<Long, Map<String, T>> getDataMetaValueMapsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds,
        Function<DataMetaModel, T> converter
    ) {
        return getDataMetaValueMapsByDataTypeAndDataIds(dataType, dataIds)
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

    Map<String, Long> getDataIdMapByMetaValues(
        String dataType,
        String metaKey,
        Collection<String> metaValues
    );

    Map<Long, String> getDataMetaValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    );

    @SuppressWarnings("unchecked")
    default  <T> Map<Long, T> getDataMetaValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey,
        Function<String, T> valueConverter
    ) {
        return getDataMetaValueMapByDataIds(
            dataType,
            dataIds,
            metaKey,
            Predicates.alwaysTrue(),
            valueConverter
        );
    }

    default <T> Map<Long, T> getDataMetaValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey,
        Predicate<String> valueFilter,
        Function<String, T> valueConverter
    ) {
        return getDataMetaValueMapByDataIds(
            dataType,
            dataIds,
            metaKey
        )
            .entrySet()
            .stream()
            .filter(e -> e.getValue() != null)
            .filter(e -> valueFilter.test(e.getValue()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> valueConverter.apply(e.getValue())
                )
            );
    }

    Map<String, String> getDataMetaValueMapByDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    );

    default Map<String, String> getLatestMetaValueMapByDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    ) {
        return metaKeys
            .parallelStream()
            .map(it ->
                EzyEntry.of(
                    it,
                    getLatestMetaValueByDataIdAndMetaKey(
                        dataType,
                        dataId,
                        it
                    )
                )
            )
            .filter(it -> it.getValue() != null)
            .collect(
                Collectors.toMap(
                    EzyEntry::getKey,
                    EzyEntry::getValue
                )
            );
    }

    Map<Long, String> getDataMetaTextValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    );

    default Map<Long, BigDecimal> getDataMetaBigDecimalValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    ) {
        return getDataMetaValueMapByDataIds(
            dataType,
            dataIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigDecimal::new
        );
    }

    default Map<Long, BigInteger> getDataMetaBigIntegerValueMapByDataIds(
        String dataType,
        Collection<Long> dataIds,
        String metaKey
    ) {
        return getDataMetaValueMapByDataIds(
            dataType,
            dataIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigInteger::new
        );
    }

    List<DataMetaModel> getMetaListByDataTypeAndDataIdAndMetaKeys(
        String dataType,
        long dataId,
        Collection<String> metaKeys
    );

    Map<Long, Map<String, String>> getDataMetaValueMapsByDataTypeAndDataIdsAndMetaKeys(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> metaKeys
    );
}
