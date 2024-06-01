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

import org.youngmonkeys.ezyplatform.model.AddUserKeywordModel;

import java.util.Collection;
import java.util.stream.Collectors;

public interface UserKeywordService {

    void addUserKeywords(
        Collection<AddUserKeywordModel> userKeywords
    );

    default void addUserKeywords(
        long userId,
        Collection<String> keywords
    ) {
        addUserKeywords(
            keywords.stream()
                .map(it ->
                    AddUserKeywordModel.builder()
                        .userId(userId)
                        .keyword(it)
                        .priority(it.length())
                        .build()
                )
                .collect(Collectors.toList())
        );
    }

    default void addUserKeyword(long userId, String keyword) {
        addUserKeyword(
            AddUserKeywordModel.builder()
                .userId(userId)
                .keyword(keyword)
                .priority(keyword.length())
                .build()
        );
    }

    void addUserKeyword(AddUserKeywordModel model);

    boolean containsUserKeyword(long userId, String keyword);
}
