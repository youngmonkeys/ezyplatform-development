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
import org.youngmonkeys.ezyplatform.model.AdminMetaModel;
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
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.util.Strings.emptyIfNull;
import static org.youngmonkeys.ezyplatform.util.Strings.toMetaValue;

@SuppressWarnings("MethodCount")
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

    void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        String metaValue
    );

    default void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        List<String> metaValues
    ) {
        metaValues
            .parallelStream()
            .filter(Objects::nonNull)
            .forEach(metaValue ->
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
        metaValues
            .parallelStream()
            .filter(Objects::nonNull)
            .forEach(metaValue ->
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

    default void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        saveAdminMetaUniqueKey(
            adminId,
            metaKey,
            metaValue,
            NULL_STRING
        );
    }

    void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        String metaValue,
        String metaTextValue
    );

    default void saveAdminMetaUniqueKeys(
        long adminId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveAdminMetaUniqueKey(
                    adminId,
                    e.getKey(),
                    e.getValue()
                )
            );
    }

    default void saveAdminMetaTextValueUniqueKeys(
        long adminId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e ->
                saveAdminMetaUniqueKey(
                    adminId,
                    e.getKey(),
                    EMPTY_STRING,
                    Strings.from(e.getValue())
                )
            );
    }

    default void saveAdminMetaValueAndTextValueUniqueKeys(
        long adminId,
        Map<String, Object> valueMap
    ) {
        valueMap
            .entrySet()
            .parallelStream()
            .filter(e -> e.getValue() != null)
            .forEach(e -> {
                String value = e.getValue().toString();
                saveAdminMetaUniqueKey(
                    adminId,
                    e.getKey(),
                    toMetaValue(value),
                    value
                );
            });
    }

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

    void deleteAdminMetaById(long id);

    void deleteAdminMetaByAdminId(long adminId);

    void deleteAdminMetaByAdminIds(
        Collection<Long> adminIds
    );
    
    void deleteAdminMetaByMetaKey(String metaKey);

    void deleteAdminMetaByAdminIdInAndMetaKeyIn(
        Collection<Long> adminIds,
        Collection<String> metaKeys
    );

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

    String getMetaTextValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    String getLatestMetaTextValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    default String getMetaTextValueByAdminIdAndMetaKeyOrDefault(
        long adminId,
        String metaKey,
        String defaultValue
    ) {
        String value = getMetaTextValueByAdminIdAndMetaKey(
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

    Map<String, String> getAdminMetaTextValues(long adminId);

    Map<Long, Map<String, AdminMetaModel>> getAdminMetaValueMapsByAdminIds(
        Collection<Long> adminIds
    );

    default  <T> Map<Long, Map<String, T>> getAdminMetaValueMapsByAdminIds(
        Collection<Long> adminIds,
        Function<AdminMetaModel, T> converter
    ) {
        return getAdminMetaValueMapsByAdminIds(adminIds)
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

    Map<String, Long> getAdminIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    );

    Map<Long, String> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    );

    @SuppressWarnings("unchecked")
    default  <T> Map<Long, T> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey,
        Function<String, T> valueConverter
    ) {
        return getAdminMetaValueMapByAdminIds(
            adminIds,
            metaKey,
            Predicates.alwaysTrue(),
            valueConverter
        );
    }

    default <T> Map<Long, T> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey,
        Predicate<String> valueFilter,
        Function<String, T> valueConverter
    ) {
        return getAdminMetaValueMapByAdminIds(
            adminIds,
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

    Map<String, String> getAdminMetaValueMapByAdminIdAndMetaKeys(
        long adminId,
        Collection<String> metaKeys
    );

    default Map<String, String> getLatestMetaValueMapByAdminIdAndMetaKeys(
        long adminId,
        Collection<String> metaKeys
    ) {
        return metaKeys
            .parallelStream()
            .map(it ->
                EzyEntry.of(
                    it,
                    getLatestMetaValueByAdminIdAndMetaKey(adminId, it)
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
    
    default Map<String, String> getLatestMetaTextValueMapByAdminIdAndMetaKeys(
        long adminId,
        Collection<String> metaKeys
    ) {
        return metaKeys
            .parallelStream()
            .map(it ->
                EzyEntry.of(
                    it,
                    getLatestMetaTextValueByAdminIdAndMetaKey(adminId, it)
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

    Map<Long, String> getAdminMetaTextValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    );

    default Map<Long, BigDecimal> getAdminMetaBigDecimalValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    ) {
        return getAdminMetaValueMapByAdminIds(
            adminIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigDecimal::new
        );
    }

    default Map<Long, BigInteger> getAdminMetaBigIntegerValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    ) {
        return getAdminMetaValueMapByAdminIds(
            adminIds,
            metaKey,
            EzyStrings::isNotBlank,
            BigInteger::new
        );
    }

    List<AdminMetaModel> getMetaListByAdminIdAndMetaKeys(
        long adminId,
        Collection<String> metaKeys
    );

    default void saveAdminJobTitle(
        long adminId,
        String jobTitle
    ) {
        saveAdminMetaUniqueKey(
            adminId,
            META_KEY_JOB_TITLE,
            emptyIfNull(jobTitle)
        );
    }

    default void saveAdminDescription(
        long adminId,
        String description
    ) {
        saveAdminMetaUniqueKey(
            adminId,
            META_KEY_DESCRIPTION,
            EMPTY_STRING,
            description
        );
    }

    default String getJobTitleByAdminId(
        long adminId
    ) {
        return getLatestMetaValueByAdminIdAndMetaKey(
            adminId,
            META_KEY_JOB_TITLE
        );
    }

    default Map<Long, String> getJobTitleMapByAdminIds(
        Collection<Long> adminIds
    ) {
        return getAdminMetaValueMapByAdminIds(
            adminIds,
            META_KEY_JOB_TITLE
        );
    }

    default String getDescriptionByAdminId(
        long adminId
    ) {
        return getLatestMetaTextValueByAdminIdAndMetaKey(
            adminId,
            META_KEY_DESCRIPTION
        );
    }
}
