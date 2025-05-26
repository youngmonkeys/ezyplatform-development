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

package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezyfox.builder.EzyBuilder;

public class DefaultNotificationFilter implements LetterFilter {
    private final String type;
    private final Long fromAdminId;
    private final Long fromUserId;
    private final String status;

    protected DefaultNotificationFilter(Builder<?> builder) {
        this.type = builder.type;
        this.fromAdminId = builder.fromAdminId;
        this.fromUserId = builder.fromUserId;
        this.status = builder.status;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (type != null) {
            answer.and("e.type = :type");
        }
        if (fromAdminId != null) {
            answer.and("e.fromAdminId = :fromAdminId");
        }
        if (fromUserId != null) {
            answer.and("e.fromUserId = :fromUserId");
        }
        if (status != null) {
            answer.and("e.status = :status");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultNotificationFilter> {
        private String type;
        private Long fromAdminId;
        private Long fromUserId;
        private String status;

        public T type(String type) {
            this.type = type;
            return (T) this;
        }

        public T fromAdminId(Long fromAdminId) {
            this.fromAdminId = fromAdminId;
            return (T) this;
        }

        public T fromUserId(Long fromUserId) {
            this.fromUserId = fromUserId;
            return (T) this;
        }

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        @Override
        public DefaultNotificationFilter build() {
            return new DefaultNotificationFilter(this);
        }
    }
}
