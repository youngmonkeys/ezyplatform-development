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

import org.youngmonkeys.ezyplatform.model.AddDataKeywordModel;
import org.youngmonkeys.ezyplatform.rx.Reactive;

import java.util.Collection;

public interface DataIndexService {

    default void addKeywords(
        String dataType,
        long dataId,
        Collection<String> keywords
    ) {
        Reactive.multiple()
            .registerConsumers(keywords, keyword ->
                addKeyword(dataType, dataId, keyword)
            )
            .blockingExecute();
    }

    default void addKeyword(
        String dataType,
        long dataId,
        String keyword
    ) {
        addKeyword(
            AddDataKeywordModel.builder()
                .dataType(dataType)
                .dataId(dataId)
                .keyword(keyword)
                .build()
        );
    }

    void addKeyword(AddDataKeywordModel model);
}
