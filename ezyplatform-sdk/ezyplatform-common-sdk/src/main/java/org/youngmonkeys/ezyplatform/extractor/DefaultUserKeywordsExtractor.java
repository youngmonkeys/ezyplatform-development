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

import java.util.LinkedHashSet;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.util.Keywords.keywordsFromEmail;
import static org.youngmonkeys.ezyplatform.util.Keywords.toKeywords;

public class DefaultUserKeywordsExtractor implements UserKeywordsExtractor {

    @Override
    public Set<String> extract(User user) {
        Set<String> answer = new LinkedHashSet<>();
        String phone = user.getPhone();
        if (isNotBlank(phone)) {
            answer.addAll(toKeywords(phone));
        }
        answer.addAll(toKeywords(user.getUsername()));
        String displayName = user.getDisplayName();
        if (isNotBlank(displayName)) {
            answer.addAll(toKeywords(displayName));
        }
        String email = user.getEmail();
        if (isNotBlank(email)) {
            answer.addAll(keywordsFromEmail(email));
        }
        answer.add(String.valueOf(user.getId()));
        return answer;
    }
}
