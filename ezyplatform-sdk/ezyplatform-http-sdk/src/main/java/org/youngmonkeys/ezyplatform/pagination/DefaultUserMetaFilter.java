/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezyfox.builder.EzyBuilder;

public class DefaultUserMetaFilter implements UserMetaFilter {
    public final Long userId;

    public DefaultUserMetaFilter(Builder builder) {
        this.userId = builder.userId;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (userId != null) {
            answer.and("e.userId = :userId");
        }
        return answer.build();
    }

    public static Builder builder() {
        return  new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultUserMetaFilter> {

        private Long userId;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public DefaultUserMetaFilter build() {
            return new DefaultUserMetaFilter(this);
        }
    }
}
