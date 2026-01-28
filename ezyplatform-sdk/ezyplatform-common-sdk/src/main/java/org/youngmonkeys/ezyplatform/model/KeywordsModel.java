/*
 * Copyright 2024 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

import static org.youngmonkeys.ezyplatform.util.Keywords.toKeywords;

@Getter
@Builder
public class KeywordsModel {
    private String likeKeyword;
    private Collection<String> keywords;

    public static KeywordsModel extract(
        String value,
        boolean byLikeOperator
    ) {
        String likeKeyword = null;
        Collection<String> keywords = null;
        if (byLikeOperator) {
            likeKeyword = value;
        } else {
            keywords = toKeywords(value, Boolean.TRUE);
        }
        return KeywordsModel.builder()
            .likeKeyword(likeKeyword)
            .keywords(keywords)
            .build();
    }
}
