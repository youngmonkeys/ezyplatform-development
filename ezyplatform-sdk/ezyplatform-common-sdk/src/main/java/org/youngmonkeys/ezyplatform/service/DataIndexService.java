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

import org.youngmonkeys.ezyplatform.model.SaveDataKeywordModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_FETCH_ROUND;

public interface DataIndexService {

    void saveKeywords(
        String dataType,
        Collection<SaveDataKeywordModel> dataKeywords
    );

    default void saveKeywords(
        String dataType,
        long dataId,
        Collection<String> keywords
    ) {
        saveKeywords(
            dataType,
            keywords
                .stream()
                .map(it ->
                    SaveDataKeywordModel.builder()
                        .dataId(dataId)
                        .keyword(it)
                        .priority(it.length())
                        .build()
                )
                .collect(Collectors.toList())
        );
    }

    default void saveKeyword(
        String dataType,
        long dataId,
        String keyword
    ) {
        saveKeyword(
            dataType,
            SaveDataKeywordModel.builder()
                .dataId(dataId)
                .keyword(keyword)
                .priority(keyword.length())
                .build()
        );
    }

    void saveKeyword(String dataType, SaveDataKeywordModel model);

    void deleteKeywordsByDataTypeAndDataId(
        String dataType,
        long dataId
    );

    void deleteKeywordsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    );

    boolean containsDataIndex(
        String dataType,
        long dataId,
        String keyword
    );
    
    List<Long> getDataIdsByTypeAndKeywords(
        String dataType,
        Collection<String> keywords,
        int limit,
        int maxFetchRound
    );

    default List<Long> getDataIdsByTypeAndKeywords(
        String dataType,
        Collection<String> keywords,
        int limit
    ) {
        return getDataIdsByTypeAndKeywords(
            dataType,
            keywords,
            limit,
            MAX_FETCH_ROUND
        );
    }

    default List<Long> getDataIdsByTypeAndKeywordPrefix(
        String dataType,
        String keywordPrefix,
        int limit
    ) {
        return getDataIdsByTypeAndKeywordPrefix(
            dataType,
            keywordPrefix,
            limit,
            MAX_FETCH_ROUND
        );
    }

    List<Long> getDataIdsByTypeAndKeywordPrefix(
        String dataType,
        String keywordPrefix,
        int limit,
        int maxFetchRound
    );
}
