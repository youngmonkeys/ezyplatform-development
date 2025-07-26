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
import lombok.Getter;

@Getter
public class DefaultDataMetaFilter implements DataMetaFilter {
    public final String dataType;
    public final Long dataId;

    public DefaultDataMetaFilter(Builder builder) {
        this.dataType = builder.dataType;
        this.dataId = builder.dataId;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (dataType != null) {
            answer.and("e.dataType = :dataType");
        }
        if (dataId != null) {
            answer.and("e.dataId = :dataId");
        }
        return answer.build();
    }

    public static Builder builder() {
        return  new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultDataMetaFilter> {

        private String dataType;
        private Long dataId;

        public Builder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder dataId(Long dataId) {
            this.dataId = dataId;
            return this;
        }

        @Override
        public DefaultDataMetaFilter build() {
            return new DefaultDataMetaFilter(this);
        }
    }
}
