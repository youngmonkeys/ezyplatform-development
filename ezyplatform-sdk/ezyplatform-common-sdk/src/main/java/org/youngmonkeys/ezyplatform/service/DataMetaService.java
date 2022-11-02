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

import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.util.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

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

    default void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        if (!containsDataMeta(
            dataType,
            dataId,
            metaKey,
            metaValue
        )) {
            saveDataMeta(
                dataType,
                dataId,
                metaKey,
                metaValue
            );
        }
    }

    default void saveDataMetaIfAbsent(
        String dataType,
        long dataId,
        String metaKey,
        List<String> metaValues
    ) {
        Reactive.single(metaValues)
            .operateItem(metaValue ->
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
        Reactive.single(metaValues)
            .operateItem(metaValue ->
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

    void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    );

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

    Map<String, Long> getDataIdMapByMetaValues(
        String dataType,
        String metaKey,
        Collection<String> metaValues
    );
}
