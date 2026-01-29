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

import java.util.Collection;

public class DefaultUserKeywordFilter implements UserKeywordFilter {
    public final String keywordPrefix;
    public final Collection<String> keywords;

    protected DefaultUserKeywordFilter(Builder<?> builder) {
        this.keywordPrefix = builder.keywordPrefix;
        this.keywords = builder.keywords;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (keywordPrefix != null) {
            answer.and("e.keyword LIKE CONCAT(:keywordPrefix,'%')");
        }
        if (keywords != null) {
            answer.and("e.keyword in :keywords");
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultUserKeywordFilter> {

        private String keywordPrefix;
        private Collection<String> keywords;

        public T keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return (T) this;
        }

        public T keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return (T) this;
        }

        @Override
        public DefaultUserKeywordFilter build() {
            return new DefaultUserKeywordFilter(this);
        }
    }
}
