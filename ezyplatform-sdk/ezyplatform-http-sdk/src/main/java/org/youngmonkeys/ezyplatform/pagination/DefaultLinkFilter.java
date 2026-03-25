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

public class DefaultLinkFilter implements LinkFilter {
    public final String linkType;
    public final String status;
    public final String sourceType;
    public final Long sourceId;
    public final String keywordPrefix;

    protected DefaultLinkFilter(Builder<?> builder) {
        this.linkType = builder.linkType;
        this.status = builder.status;
        this.sourceType = builder.sourceType;
        this.sourceId = builder.sourceId;
        this.keywordPrefix = builder.keywordPrefix;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (linkType != null) {
            answer.and("e.linkType = :linkType");
        }
        if (sourceType != null) {
            answer.and("e.sourceType = :sourceType");
        }
        if (sourceId != null) {
            answer.and("e.sourceId = :sourceId");
        }
        if (status != null) {
            answer.and("e.status = :status");
        }
        if (keywordPrefix != null) {
            answer.and(
                "(e.linkUri LIKE CONCAT(:keywordPrefix,'%') " +
                    "OR e.description LIKE CONCAT(:keywordPrefix,'%'))"
            );
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultLinkFilter> {

        private String linkType;
        private String status;
        private String sourceType;
        private Long sourceId;
        private String keywordPrefix;

        public T linkType(String linkType) {
            this.linkType = linkType;
            return (T) this;
        }

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        public T sourceType(String sourceType) {
            this.sourceType = sourceType;
            return (T) this;
        }

        public T sourceId(Long sourceId) {
            this.sourceId = sourceId;
            return (T) this;
        }

        public T keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return (T) this;
        }

        @Override
        public DefaultLinkFilter build() {
            return new DefaultLinkFilter(this);
        }
    }
}
