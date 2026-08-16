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

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.Next;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataIndex;
import org.youngmonkeys.ezyplatform.model.DataTypeIdModel;
import org.youngmonkeys.ezyplatform.model.SaveDataKeywordModel;
import org.youngmonkeys.ezyplatform.repo.DataIndexRepository;
import org.youngmonkeys.ezyplatform.result.DataTypeIdResult;
import org.youngmonkeys.ezyplatform.result.IdResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;
import static org.youngmonkeys.ezyplatform.util.Keywords.toKeywords;

@AllArgsConstructor
public class DefaultDataIndexService
        extends EzyLoggable
        implements DataIndexService {

    private final DataIndexRepository dataIndexRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void saveKeyword(
        String dataType,
        SaveDataKeywordModel model
    ) {
        long dataId = model.getDataId();
        String keyword = model.getKeyword();
        DataIndex entity = dataIndexRepository
            .findByDataTypeAndDataIdAndKeyword(
                dataType,
                dataId,
                keyword
            );
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(dataType, model);
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        try {
            dataIndexRepository.save(entity);
        } catch (Exception e) {
            logger.info(
                "save keyword: {} of data type: {} and id: {} failed",
                keyword,
                dataType,
                dataId
            );
        }
    }

    @Override
    public void saveKeywords(
        String dataType,
        Collection<SaveDataKeywordModel> dataKeywords
    ) {
        for (SaveDataKeywordModel model : dataKeywords) {
            saveKeyword(dataType, model);
        }
    }

    @Override
    public void deleteKeywordsByDataTypeAndDataId(
        String dataType,
        long dataId
    ) {
        dataIndexRepository.deleteByDataTypeAndDataId(
            dataType,
            dataId
        );
    }

    @Override
    public void deleteKeywordsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    ) {
        if (!dataIds.isEmpty()) {
            dataIndexRepository.deleteByDataTypeAndDataIdIn(
                dataType,
                dataIds
            );
        }
    }

    @Override
    public boolean containsDataIndex(
        String dataType,
        long dataId,
        String keyword
    ) {
        return dataIndexRepository.findByDataTypeAndDataIdAndKeyword(
            dataType,
            dataId,
            keyword
        ) != null;
    }

    @Override
    public List<Long> getDataIdsByTypeAndKeywords(
        String dataType,
        Collection<String> keywords,
        int limit,
        int maxFetchRound
    ) {
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> exclusiveDataIds = Collections.singletonList(ZERO_LONG);
        List<Long> dataIds = new ArrayList<>();
        int fetchedRound = ZERO;
        while (fetchedRound < maxFetchRound && dataIds.size() < limit) {
            List<Long> fetchedDataIds = dataIndexRepository
                .findDataIdsByDataTypeAndKeywordInAndDataIdNotInOrderByPriorityDescIdDesc(
                    dataType,
                    keywords,
                    exclusiveDataIds,
                    Next.limit(limit)
                )
                .stream()
                .map(IdResult::getId)
                .distinct()
                .collect(Collectors.toList());
            dataIds.addAll(fetchedDataIds);
            exclusiveDataIds = dataIds;
            if (fetchedDataIds.size() < limit) {
                break;
            }
            ++fetchedRound;
        }
        return dataIds.size() <= limit ? dataIds : dataIds.subList(0, limit);
    }

    @Override
    public List<Long> getDataIdsByTypeAndKeywordPrefix(
        String dataType,
        String keywordPrefix,
        int limit,
        int maxFetchRound
    ) {
        if (isBlank(keywordPrefix)) {
            return Collections.emptyList();
        }
        List<Long> exclusiveDataIds = Collections.singletonList(ZERO_LONG);
        List<Long> dataIds = new ArrayList<>();
        int fetchedRound = ZERO;
        while (fetchedRound < maxFetchRound && dataIds.size() < limit) {
            List<Long> fetchedDataIds = dataIndexRepository
                .findDataIdsByDataTypeAndKeywordPrefixAndDataIdNotInOrderByPriorityDescIdDesc(
                    dataType,
                    keywordPrefix,
                    exclusiveDataIds,
                    Next.limit(limit)
                )
                .stream()
                .map(IdResult::getId)
                .distinct()
                .collect(Collectors.toList());
            dataIds.addAll(fetchedDataIds);
            exclusiveDataIds = dataIds;
            if (fetchedDataIds.size() < limit) {
                break;
            }
            ++fetchedRound;
        }
        return dataIds.size() <= limit ? dataIds : dataIds.subList(0, limit);
    }

    @Override
    public List<DataTypeIdModel> getDataTypeIdsByDataTypesAndKeywordPrefix(
        Collection<String> dataTypes,
        String keywordPrefix,
        long limit,
        long overFetchFactor
    ) {
        if (dataTypes.isEmpty() || isBlank(keywordPrefix)) {
            return Collections.emptyList();
        }
        List<DataTypeIdResult> results = dataIndexRepository
            .findDataTypeIdsByDataTypeInAndKeywordPrefixOrderByPriorityDescIdDesc(
                dataTypes,
                keywordPrefix,
                Next.limit(limit * overFetchFactor)
            );
        Set<DataTypeIdModel> distinctDataTypeIdModels =
            new LinkedHashSet<>();
        for (DataTypeIdResult result : results) {
            distinctDataTypeIdModels.add(
                new DataTypeIdModel(
                    result.getDataType(),
                    result.getDataId()
                )
            );
            if (distinctDataTypeIdModels.size() >= limit) {
                break;
            }
        }
        return new ArrayList<>(distinctDataTypeIdModels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataTypeIdModel> searchDataIdsByReciprocalRankFusion(
        Collection<String> dataTypes,
        String query,
        int limit,
        String splitPattern,
        int minKeywordLength,
        Set<String> stopWords,
        int maxKeywords,
        int dataRankFusionConstant
    ) {
        if (dataTypes.isEmpty() || isBlank(query)) {
            return Collections.emptyList();
        }
        List<String> keywords = toKeywords(
            query,
            splitPattern,
            minKeywordLength,
            stopWords,
            maxKeywords
        );
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }
        Map<DataTypeIdModel, Double> scoresByDataTypeId = new HashMap<>();
        for (String keyword : keywords) {
            List<DataTypeIdModel> dataTypeIds =
                getDataTypeIdsByDataTypesAndKeywordPrefix(
                    dataTypes,
                    keyword,
                    limit
                );
            for (int rank = 0; rank < dataTypeIds.size(); ++rank) {
                double score = 1.0 / (dataRankFusionConstant + rank + 1);
                scoresByDataTypeId.merge(dataTypeIds.get(rank), score, Double::sum);
            }
        }
        return scoresByDataTypeId.entrySet()
            .stream()
            .sorted(
                Map.Entry
                    .<DataTypeIdModel, Double>comparingByValue()
                    .reversed()
            )
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
