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

import org.youngmonkeys.ezyplatform.model.DataTypeIdModel;
import org.youngmonkeys.ezyplatform.model.SaveDataKeywordModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.DEFAULT_DATA_INDEX_OVER_FETCH_FACTOR;
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

    default List<DataTypeIdModel> getDataTypeIdsByDataTypesAndKeywordPrefix(
        Collection<String> dataTypes,
        String keywordPrefix,
        int limit
    ) {
        return getDataTypeIdsByDataTypesAndKeywordPrefix(
            dataTypes,
            keywordPrefix,
            limit,
            DEFAULT_DATA_INDEX_OVER_FETCH_FACTOR
        );
    }

    List<DataTypeIdModel> getDataTypeIdsByDataTypesAndKeywordPrefix(
        Collection<String> dataTypes,
        String keywordPrefix,
        long limit,
        long overFetchFactor
    );

    /**
     * Equivalent to
     * {@link #searchDataIdsByReciprocalRankFusion(Collection, String, int, String, int, Set, int, int)}
     * with {@code splitPattern}, {@code minKeywordLength},
     * {@code stopWords}, {@code maxKeywords} and
     * {@code dataRankFusionConstant} taken from the current settings.
     *
     * @param dataTypes the {@code data_type} values in {@code ezy_data_indices}
     *                   to search across
     * @param query the free-text query to search for, e.g. a chat message
     * @param limit the maximum number of results to return
     * @return data type/id pairs ranked by fused relevance, descending;
     *         empty if {@code dataTypes} is empty, {@code query} is blank,
     *         or no keyword could be extracted from it
     */
    List<DataTypeIdModel> searchDataIdsByReciprocalRankFusion(
        Collection<String> dataTypes,
        String query,
        int limit
    );

    /**
     * Searches {@code ezy_data_indices} for the data type/id pairs most
     * relevant to a free-text query (e.g. a chat message), using
     * Reciprocal Rank Fusion (RRF) to combine per-keyword rankings.
     *
     * <p>Algorithm:
     * <ol>
     *     <li>The query is trimmed, lower-cased and split by
     *     {@code splitPattern} into tokens (see
     *     {@link org.youngmonkeys.ezyplatform.util.Keywords#toKeywords(String, String, int, Set, int)});
     *     tokens shorter than {@code minKeywordLength} or contained in
     *     {@code stopWords} are dropped, duplicates are removed, the
     *     remaining tokens are sorted longest-first (most specific first),
     *     and the result is capped at {@code maxKeywords} tokens so a
     *     long query cannot fan out into unbounded lookups.</li>
     *     <li>Each keyword is searched independently as a prefix match
     *     against {@code ezy_data_indices} across all of {@code dataTypes}
     *     at once (see
     *     {@link #getDataTypeIdsByDataTypesAndKeywordPrefix(Collection, String, int)}),
     *     returning up to {@code limit} distinct data type/id pairs already
     *     ranked by the index's {@code priority} column, most specific
     *     match first.</li>
     *     <li>The per-keyword ranked lists are fused with RRF: for a data
     *     type/id pair at zero-based {@code rank} in a keyword's result
     *     list, it earns {@code 1 / (dataRankFusionConstant + rank + 1)}
     *     points. Points from all keywords are summed per data type/id
     *     pair, so a pair that ranks highly under several keywords
     *     outscores one that ranks highly under only one. RRF is used
     *     instead of a raw priority sum because the underlying prefix
     *     search only exposes the rank/order of each keyword's matches,
     *     not a comparable numeric score.</li>
     *     <li>Data type/id pairs are sorted by fused score, descending,
     *     and truncated to {@code limit}.</li>
     * </ol>
     *
     * @param dataTypes the {@code data_type} values in {@code ezy_data_indices}
     *                   to search across
     * @param query the free-text query to search for, e.g. a chat message
     * @param limit the maximum number of results to return
     * @param splitPattern the regex used to split {@code query} into tokens
     * @param minKeywordLength the minimum token length to keep as a keyword
     * @param stopWords tokens to discard even if they meet
     *                   {@code minKeywordLength}
     * @param maxKeywords the maximum number of keywords extracted from
     *                     {@code query} to fuse over
     * @param dataRankFusionConstant the {@code k} constant in the RRF
     *                                formula {@code 1 / (k + rank + 1)}
     * @return data type/id pairs ranked by fused relevance, descending;
     *         empty if {@code dataTypes} is empty, {@code query} is blank,
     *         or no keyword could be extracted from it
     */
    List<DataTypeIdModel> searchDataIdsByReciprocalRankFusion(
        Collection<String> dataTypes,
        String query,
        int limit,
        String splitPattern,
        int minKeywordLength,
        Set<String> stopWords,
        int maxKeywords,
        int dataRankFusionConstant
    );
}
