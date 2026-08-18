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

public class DefaultUserMetaFilter implements UserMetaFilter {
    public final Long userId;
    public final String metaKey;
    public final String exclusiveMetaKey;
    public final Collection<String> exclusiveMetaKeys;
    public final String metaValue;
    public final String likeKeyword;

    public DefaultUserMetaFilter(Builder builder) {
        this.userId = builder.userId;
        this.metaKey = builder.metaKey;
        this.exclusiveMetaKey = builder.exclusiveMetaKey;
        this.exclusiveMetaKeys = builder.exclusiveMetaKeys;
        this.metaValue = builder.metaValue;
        this.likeKeyword = builder.likeKeyword;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (userId != null) {
            answer.and("e.userId = :userId");
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
            answer.and(
                "(e.metaValue LIKE CONCAT(:likeKeyword, '%')" +
                " OR e.metaTextValue LIKE CONCAT(:likeKeyword, '%'))"
            );
        }
        return answer.build();
    }

    public static Builder builder() {
        return  new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultUserMetaFilter> {

        private Long userId;
        private String metaKey;
        private String exclusiveMetaKey;
        private Collection<String> exclusiveMetaKeys;
        private String metaValue;
        private String likeKeyword;

        public Builder userId(Long userId) {
            this.userId = userId;
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

        @Override
        public DefaultUserMetaFilter build() {
            return new DefaultUserMetaFilter(this);
        }
    }
}
