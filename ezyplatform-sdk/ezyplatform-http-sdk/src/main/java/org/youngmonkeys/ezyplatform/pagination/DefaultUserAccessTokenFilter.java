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

public class DefaultUserAccessTokenFilter
    implements UserAccessTokenFilter {

    public final Long userId;
    public final String tokenType;
    public final String status;

    protected DefaultUserAccessTokenFilter(Builder<?> builder) {
        this.userId = builder.userId;
        this.tokenType = builder.tokenType;
        this.status = builder.status;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (userId != null) {
            answer.and("e.userId = :userId");
        }
        if (tokenType != null) {
            answer.and("e.tokenType = :tokenType");
        }
        if (status != null) {
            answer.and("e.status = :status");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultUserAccessTokenFilter> {
        private Long userId;
        private String tokenType;
        private String status;

        public T userId(Long userId) {
            this.userId = userId;
            return (T) this;
        }

        public T tokenType(String tokenType) {
            this.tokenType = tokenType;
            return (T) this;
        }

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        @Override
        public DefaultUserAccessTokenFilter build() {
            return new DefaultUserAccessTokenFilter(this);
        }
    }
}
