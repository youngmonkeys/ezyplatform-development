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

public interface UserMetaService {

    void saveUserMeta(
        long userId,
        String metaKey,
        String metaValue
    );

    default void saveUserMeta(
        long userId,
        String metaKey,
        Object metaValue
    ) {
        saveUserMeta(
            userId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveUserMeta(
        long userId,
        String metaKey,
        List<String> metaValues
    );

    default void saveUserMeta(
        long userId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        saveUserMeta(
            userId,
            metaKey,
            (List<String>) newArrayList(
                metaValues,
                Strings::from
            )
        );
    }

    default void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        Object metaValue
    ) {
        saveUserMetaIfAbsent(
            userId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    default void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        String metaValue
    ) {
        if (!containsUserMeta(userId, metaKey, metaValue)) {
            saveUserMeta(userId, metaKey, metaValue);
        }
    }

    default void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        List<String> metaValues
    ) {
        Reactive.single(metaValues)
            .operateItem(metaValue ->
                saveUserMetaIfAbsent(
                    userId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        Collection<Object> metaValues
    ) {
        Reactive.single(metaValues)
            .operateItem(metaValue ->
                saveUserMetaIfAbsent(
                    userId,
                    metaKey,
                    metaValue
                )
            );
    }

    default void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        Object metaValue
    ) {
        saveUserMetaUniqueKey(
            userId,
            metaKey,
            Strings.from(metaValue)
        );
    }

    void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue
    );

    BigDecimal increaseUserMetaValue(
        long userId,
        String metaKey,
        BigDecimal value
    );

    default BigInteger increaseUserMetaValue(
        long userId,
        String metaKey,
        BigInteger value
    ) {
        return increaseUserMetaValue(
            userId,
            metaKey,
            new BigDecimal(value)
        ).toBigInteger();
    }

    boolean containsUserMeta(
        long userId,
        String metaKey,
        String metaValue
    );

    long getUserIdByMeta(
        String metaKey,
        String metaValue
    );

    default long getUserIdByMeta(
        String metaKey,
        Object metaValue
    ) {
        return getUserIdByMeta(
            metaKey,
            Strings.from(metaValue)
        );
    }

    String getMetaValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    );

    String getLatestMetaValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    );

    default String getMetaValueByUserIdAndMetaKeyOrDefault(
        long userId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaValueByUserIdAndMetaKey(
            userId,
            metaKey
        );
        return value != null ? value : defaultValue;
    }

    default BigDecimal getMetaDecimalValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        String value = getMetaValueByUserIdAndMetaKey(
            userId,
            metaKey
        );
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    default BigInteger getMetaIntegerValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        String value = getMetaValueByUserIdAndMetaKey(
            userId,
            metaKey
        );
        return value != null ? new BigInteger(value) : BigInteger.ZERO;
    }

    List<String> getMetaValuesByUserIdAndMetaKey(
        long userId,
        String metaKey,
        int limit
    );

    default <T> List<T> getMetaValuesByUserIdAndMetaKey(
        long userId,
        String metaKey,
        int limit,
        Function<String, T> valueConverter
    ) {
        return newArrayList(
            getMetaValuesByUserIdAndMetaKey(
                userId,
                metaKey,
                limit
            ),
            valueConverter
        );
    }

    Map<String, String> getUserMetaValues(long userId);

    Map<String, Long> getUserIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    );

    Map<Long, String> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    );

    default <V> Map<Long, V> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey,
        Function<String, V> valueConverter
    ) {
        return newHashMapNewValues(
            getUserMetaValueMapByUserIds(userIds, metaKey),
            valueConverter
        );
    }
}
