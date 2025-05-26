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

public class DefaultLetterReceiverFilter implements LetterReceiverFilter {
    public final Long toUserId;
    public final Long toAdminId;

    protected DefaultLetterReceiverFilter(Builder<?> builder) {
        this.toUserId = builder.toUserId;
        this.toAdminId = builder.toAdminId;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (toUserId != null) {
            answer.and("e.toUserId = :toUserId");
        }
        if (toAdminId != null) {
            answer.and("e.toAdminId = :toAdminId");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultLetterReceiverFilter> {

        private Long toUserId;
        private Long toAdminId;

        public T toUserId(Long toUserId) {
            this.toUserId = toUserId;
            return (T) this;
        }

        public T toAdminId(Long toAdminId) {
            this.toAdminId = toAdminId;
            return (T) this;
        }

        @Override
        public DefaultLetterReceiverFilter build() {
            return new DefaultLetterReceiverFilter(this);
        }
    }
}
