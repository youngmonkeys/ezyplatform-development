/*
 * Copyright 2026 youngmonkeys.org
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

public class DefaultAccessTokenMetaFilter
    implements AccessTokenMetaFilter {

    public final String target;
    public final String tokenType;

    protected DefaultAccessTokenMetaFilter(Builder<?> builder) {
        this.target = builder.target;
        this.tokenType = builder.tokenType;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (target != null) {
            answer.and("e.target = :target");
        }
        if (tokenType != null) {
            answer.and("e.tokenType = :tokenType");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultAccessTokenMetaFilter> {
        private String target;
        private String tokenType;

        public T target(String target) {
            this.target = target;
            return (T) this;
        }

        public T tokenType(String tokenType) {
            this.tokenType = tokenType;
            return (T) this;
        }

        @Override
        public DefaultAccessTokenMetaFilter build() {
            return new DefaultAccessTokenMetaFilter(this);
        }
    }
}
