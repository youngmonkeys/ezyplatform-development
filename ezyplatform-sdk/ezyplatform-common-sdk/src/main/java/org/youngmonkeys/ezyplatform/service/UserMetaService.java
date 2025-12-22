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
import org.youngmonkeys.ezyplatform.model.UserMetaModel;
import org.youngmonkeys.ezyplatform.util.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@SuppressWarnings("MethodCount")
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

    void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        String metaValue
    );

    default void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        List<String> metaValues
    ) {
        metaValues
            .parallelStream()
            .forEach(metaValue ->
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
        metaValues
            .parallelStream()
            .filter(Objects::nonNull)
            .forEach(metaValue ->
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

    default void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue
    ) {
        saveUserMetaUniqueKey(
            userId,
            metaKey,
            metaValue,
            null
        );
    }

    void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue,
        String metaTextValue
    );

    default void saveUserMetaUniqueKeys(
        long userId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveUserMetaUniqueKey(
                    userId,
                    e.getKey(),
                    e.getValue()
                )
            );
    }

    default void saveUserMetaTextValueUniqueKeys(
        long userId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveUserMetaUniqueKey(
                    userId,
                    e.getKey(),
                    EMPTY_STRING,
                    Strings.from(e.getValue())
                )
            );
    }

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

    void deleteUserMetaById(long id);

    void deleteUserMetaByUserId(long userId);

    void deleteUserMetaByUserIds(
        Collection<Long> userIds
    );

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

    Set<Long> getUserIdsByMeta(
        String metaKey,
        String metaValue
    );

    default Set<Long> getUserIdsByMeta(
        String metaKey,
        Object metaValue
    ) {
        return getUserIdsByMeta(
            metaKey,
            String.valueOf(metaValue)
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

    String getMetaTextValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    );

    String getLatestMetaTextValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    );

    default String getMetaTextValueByUserIdAndMetaKeyOrDefault(
        long userId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaTextValueByUserIdAndMetaKey(
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

    Map<String, String> getUserMetaTextValues(long userId);
    
    Map<Long, Map<String, UserMetaModel>> getUserMetaValueMapsByUserIds(
        Collection<Long> userIds
    );

    default  <T> Map<Long, Map<String, T>> getUserMetaValueMapsByUserIds(
        Collection<Long> userIds,
        Function<UserMetaModel, T> converter
    ) {
        return getUserMetaValueMapsByUserIds(userIds)
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

    Map<String, Long> getUserIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    );

    Map<Long, String> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    );

    @SuppressWarnings("unchecked")
    default  <T> Map<Long, T> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey,
        Function<String, T> valueConverter
    ) {
        return getUserMetaValueMapByUserIds(
            userIds,
            metaKey,
            Predicates.alwaysTrue(),
            valueConverter
        );
    }

    default <T> Map<Long, T> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey,
        Predicate<String> valueFilter,
        Function<String, T> valueConverter
    ) {
        return getUserMetaValueMapByUserIds(
            userIds,
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

    Map<String, String> getUserMetaValueMapByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    );

    default Map<String, String> getLatestMetaValueMapByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    ) {
        return metaKeys
            .parallelStream()
            .map(it ->
                EzyEntry.of(
                    it,
                    getLatestMetaValueByUserIdAndMetaKey(userId, it)
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

    default Map<String, String> getLatestMetaTextValueMapByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    ) {
        return metaKeys
            .parallelStream()
            .map(it ->
                EzyEntry.of(
                    it,
                    getLatestMetaTextValueByUserIdAndMetaKey(userId, it)
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

    Map<Long, String> getUserMetaTextValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    );

    default Map<Long, BigDecimal> getUserMetaBigDecimalValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    ) {
        return getUserMetaValueMapByUserIds(
            userIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigDecimal::new
        );
    }

    default Map<Long, BigInteger> getUserMetaBigIntegerValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    ) {
        return getUserMetaValueMapByUserIds(
            userIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigInteger::new
        );
    }

    List<UserMetaModel> getMetaListByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    );
}
