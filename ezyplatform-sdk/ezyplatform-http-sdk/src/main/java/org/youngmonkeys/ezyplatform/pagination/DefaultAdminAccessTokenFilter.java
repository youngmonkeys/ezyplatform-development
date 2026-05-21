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

public class DefaultAdminAccessTokenFilter
    implements AdminAccessTokenFilter {

    public final Long adminId;
    public final String tokenType;
    public final String status;

    protected DefaultAdminAccessTokenFilter(Builder<?> builder) {
        this.adminId = builder.adminId;
        this.tokenType = builder.tokenType;
        this.status = builder.status;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (adminId != null) {
            answer.and("e.adminId = :adminId");
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
        implements EzyBuilder<DefaultAdminAccessTokenFilter> {
        private Long adminId;
        private String tokenType;
        private String status;

        public T adminId(Long adminId) {
            this.adminId = adminId;
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
        public DefaultAdminAccessTokenFilter build() {
            return new DefaultAdminAccessTokenFilter(this);
        }
    }
}
