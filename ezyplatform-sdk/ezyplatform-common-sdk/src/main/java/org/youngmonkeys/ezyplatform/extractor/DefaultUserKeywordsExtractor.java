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

package org.youngmonkeys.ezyplatform.extractor;

import org.youngmonkeys.ezyplatform.entity.User;

import java.util.HashSet;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.util.Keywords.keywordFromEmail;
import static org.youngmonkeys.ezyplatform.util.Keywords.toKeywords;

public class DefaultUserKeywordsExtractor implements UserKeywordsExtractor {

    @Override
    public Set<String> extract(User user) {
        Set<String> answer = new HashSet<>();
        answer.add(String.valueOf(user.getId()));
        answer.add(user.getUsername());
        if (isNotBlank(user.getEmail())) {
            answer.add(user.getEmail());
            String keyword = keywordFromEmail(user.getEmail());
            if (isNotBlank(keyword)) {
                answer.add(keyword);
            }
        }
        if (isNotBlank(user.getDisplayName())) {
            answer.addAll(toKeywords(user.getDisplayName()));
        }
        if (isNotBlank(user.getPhone())) {
            answer.add(user.getPhone());
        }
        return answer;
    }
}
