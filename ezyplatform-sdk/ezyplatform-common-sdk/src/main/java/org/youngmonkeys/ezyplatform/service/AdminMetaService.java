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
import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;

public interface AdminMetaService {

    void saveAdminMeta(
        long adminId,
        String metaKey,
        String metaValue
    );

    default void saveAdminMeta(
        long adminId,
        String metaKey,
        Object metaValue
    ) {
        saveAdminMeta(
            adminId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveAdminMeta(
        long adminId,
        String metaKey,
        List<String> metaValues
    );

    default void saveAdminMeta(
        long adminId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        saveAdminMeta(
            adminId,
            metaKey,
            (List<String>) newArrayList(
                metaValues,
                Strings::from
            )
        );
    }

    default void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        Object metaValue
    ) {
        saveAdminMetaIfAbsent(
            adminId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    default void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        if (!containsAdminMeta(adminId, metaKey, metaValue)) {
            saveAdminMeta(adminId, metaKey, metaValue);
        }
    }

    default void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        List<String> metaValues
    ) {
        Reactive.single(metaValues)
            .operateItem(metaValue ->
                saveAdminMetaIfAbsent(
                    adminId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        Reactive.single(metaValues)
            .operateItem(metaValue ->
                saveAdminMetaIfAbsent(
                    adminId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        Object metaValue
    ) {
        saveAdminMetaUniqueKey(
            adminId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        String metaValue
    );

    BigDecimal increaseAdminMetaValue(
        long adminId,
        String metaKey,
        BigDecimal value
    );

    default BigInteger increaseAdminMetaValue(
        long adminId,
        String metaKey,
        BigInteger value
    ) {
        return increaseAdminMetaValue(
            adminId,
            metaKey,
            new BigDecimal(value)
        ).toBigInteger();
    }

    boolean containsAdminMeta(
        long adminId,
        String metaKey,
        String metaValue
    );

    long getAdminIdByMeta(
        String metaKey,
        String metaValue
    );

    default long getAdminIdByMeta(
        String metaKey,
        Object metaValue
    ) {
        return getAdminIdByMeta(
            metaKey,
            Strings.from(metaValue)
        );
    }

    String getMetaValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    String getLatestMetaValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    default String getMetaValueByAdminIdAndMetaKeyOrDefault(
        long adminId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaValueByAdminIdAndMetaKey(
            adminId,
            metaKey
        );
        return value != null ? value : defaultValue;
    }

    default BigDecimal getMetaDecimalValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        String value = getMetaValueByAdminIdAndMetaKey(
            adminId,
            metaKey
        );
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    default BigInteger getMetaIntegerValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        String value = getMetaValueByAdminIdAndMetaKey(
            adminId,
            metaKey
        );
        return value != null ? new BigInteger(value) : BigInteger.ZERO;
    }

    List<String> getMetaValuesByAdminIdAndMetaKey(
        long adminId,
        String metaKey,
        int limit
    );

    default <T> List<T> getMetaValuesByAdminIdAndMetaKey(
        long adminId,
        String metaKey,
        int limit,
        Function<String, T> valueConverter
    ) {
        return newArrayList(
            getMetaValuesByAdminIdAndMetaKey(
                adminId,
                metaKey,
                limit
            ),
            valueConverter
        );
    }

    Map<String, String> getAdminMetaValues(long adminId);

    Map<String, Long> getAdminIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    );

    Map<Long, String> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    );

    default <V> Map<Long, V> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey,
        Function<String, V> valueConverter
    ) {
        return newHashMapNewValues(
            getAdminMetaValueMapByAdminIds(
                adminIds,
                metaKey
            ),
            valueConverter
        );
    }
}
