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
public class DefaultAdminMetaFilter implements AdminMetaFilter {
    public final Long adminId;

    public DefaultAdminMetaFilter(Builder builder) {
        this.adminId = builder.adminId;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (adminId != null) {
            answer.and("e.adminId = :adminId");
        }
        return answer.build();
    }

    public static Builder builder() {
        return  new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultAdminMetaFilter> {

        private Long adminId;

        public Builder adminId(Long adminId) {
            this.adminId = adminId;
            return this;
        }

        @Override
        public DefaultAdminMetaFilter build() {
            return new DefaultAdminMetaFilter(this);
        }
    }
}
