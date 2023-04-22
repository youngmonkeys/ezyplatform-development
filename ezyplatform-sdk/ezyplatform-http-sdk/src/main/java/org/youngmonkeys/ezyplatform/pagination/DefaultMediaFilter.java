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
import lombok.Getter;

@Getter
public class DefaultMediaFilter implements MediaFilter {
    public final Long ownerUserId;

    protected DefaultMediaFilter(Builder<?> builder) {
        this.ownerUserId = builder.ownerUserId;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (ownerUserId != null) {
            answer.and("e.ownerUserId = :ownerUserId");
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultMediaFilter> {

        private Long ownerUserId;

        public T ownerUserId(Long ownerUserId) {
            this.ownerUserId = ownerUserId;
            return (T) this;
        }

        @Override
        public DefaultMediaFilter build() {
            return new DefaultMediaFilter(this);
        }
    }
}
