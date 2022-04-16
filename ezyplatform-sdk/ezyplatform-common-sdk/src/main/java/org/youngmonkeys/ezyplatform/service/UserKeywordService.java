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

import org.youngmonkeys.ezyplatform.entity.AddUserKeywordModel;
import org.youngmonkeys.ezyplatform.rx.Reactive;

import java.util.Collection;

public interface UserKeywordService {

    default void addUserKeywords(
        long userId,
        Collection<String> keywords
    ) {
        Reactive.multiple()
            .registerConsumers(keywords, keyword ->
                addUserKeyword(userId, keyword)
            )
            .blockingExecute();
    }

    default void addUserKeyword(long userId, String keyword) {
        addUserKeyword(
            AddUserKeywordModel.builder()
                .userId(userId)
                .keyword(keyword)
                .build()
        );
    }

    void addUserKeyword(AddUserKeywordModel model);
}
