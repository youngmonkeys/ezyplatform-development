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
import org.youngmonkeys.ezyplatform.entity.DataType;

public class DefaultSettingFilter implements SettingFilter {
    public final DataType dataType;
    public final String likeKeyword;

    protected DefaultSettingFilter(Builder<?> builder) {
        this.dataType = builder.dataType;
        this.likeKeyword = builder.likeKeyword;
    }

    @SuppressWarnings("MethodLength")
    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (dataType != null) {
            answer.and("e.dataType = :dataType");
        }
        if (likeKeyword != null) {
            answer.and("e.name LIKE CONCAT('%',:likeKeyword,'%')");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultSettingFilter> {
        private DataType dataType;
        private String likeKeyword;

        public T dataType(DataType dataType) {
            this.dataType = dataType;
            return (T) this;
        }

        public T likeKeyword(String likeKeyword) {
            this.likeKeyword = likeKeyword;
            return (T) this;
        }

        @Override
        public DefaultSettingFilter build() {
            return new DefaultSettingFilter(this);
        }
    }
}
