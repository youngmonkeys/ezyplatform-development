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

import java.util.Collection;

public class DefaultDataMetaFilter implements DataMetaFilter {
    public final String dataType;
    public final Long dataId;
    public final String metaKey;
    public final String exclusiveMetaKey;
    public final Collection<String> exclusiveMetaKeys;
    public final String metaValue;
    public final String likeKeyword;
    public final String keywordPrefix;

    public DefaultDataMetaFilter(Builder builder) {
        this.dataType = builder.dataType;
        this.dataId = builder.dataId;
        this.metaKey = builder.metaKey;
        this.exclusiveMetaKey = builder.exclusiveMetaKey;
        this.exclusiveMetaKeys = builder.exclusiveMetaKeys;
        this.metaValue = builder.metaValue;
        this.likeKeyword = builder.likeKeyword;
        this.keywordPrefix = builder.keywordPrefix;
    }

    @Override
    public void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {
        if (keywordPrefix != null) {
            queryString.append(" INNER JOIN DataIndex k ON e.dataId = k.dataId");
        }
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
        if (metaKey != null) {
            answer.and("e.metaKey = :metaKey");
        }
        if (exclusiveMetaKey != null) {
            answer.and("e.metaKey <> :exclusiveMetaKey");
        }
        if (exclusiveMetaKeys != null) {
            answer.and("e.metaKey NOT IN :exclusiveMetaKeys");
        }
        if (metaValue != null) {
            answer.and("e.metaValue = :metaValue");
        }
        if (likeKeyword != null) {
            answer.and("e.metaValue LIKE CONCAT(:likeKeyword, '%')");
        }
        if (keywordPrefix != null) {
            answer.and("k.dataType = e.dataType");
            answer.and("k.keyword LIKE CONCAT(:keywordPrefix,'%')");
        }
        return answer.build();
    }

    public static Builder builder() {
        return  new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultDataMetaFilter> {

        private String dataType;
        private Long dataId;
        private String metaKey;
        private String exclusiveMetaKey;
        private Collection<String> exclusiveMetaKeys;
        protected String metaValue;
        private String likeKeyword;
        private String keywordPrefix;

        public Builder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder dataId(Long dataId) {
            this.dataId = dataId;
            return this;
        }

        public Builder metaKey(String metaKey) {
            this.metaKey = metaKey;
            return this;
        }

        public Builder exclusiveMetaKey(String exclusiveMetaKey) {
            this.exclusiveMetaKey = exclusiveMetaKey;
            return this;
        }

        public Builder exclusiveMetaKeys(
            Collection<String> exclusiveMetaKeys
        ) {
            this.exclusiveMetaKeys = exclusiveMetaKeys;
            return this;
        }

        public Builder metaValue(String metaValue) {
            this.metaValue = metaValue;
            return this;
        }

        public Builder likeKeyword(String likeKeyword) {
            this.likeKeyword = likeKeyword;
            return this;
        }

        public Builder keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return this;
        }

        @Override
        public DefaultDataMetaFilter build() {
            return new DefaultDataMetaFilter(this);
        }
    }
}
