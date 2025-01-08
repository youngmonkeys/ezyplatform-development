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
import org.youngmonkeys.ezyplatform.entity.MediaType;

@Getter
public class DefaultMediaFilter implements MediaFilter {
    public final MediaType type;
    public final Long ownerAdminId;
    public final Long ownerUserId;
    public final String prefixKeyword;
    public final String likeKeyword;

    protected DefaultMediaFilter(Builder<?> builder) {
        this.type = builder.type;
        this.ownerAdminId = builder.ownerAdminId;
        this.ownerUserId = builder.ownerUserId;
        this.prefixKeyword = builder.prefixKeyword;
        this.likeKeyword = builder.likeKeyword;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (type != null) {
            answer.and("e.type = :type");
        }
        if (ownerAdminId != null) {
            answer.and("e.ownerAdminId = :ownerAdminId");
        }
        if (ownerUserId != null) {
            answer.and("e.ownerUserId = :ownerUserId");
        }
        if (prefixKeyword != null) {
            answer.and("e.originalName LIKE CONCAT(:prefixKeyword,'%')");
        }
        if (likeKeyword != null) {
            answer.and("e.originalName LIKE CONCAT('%',:likeKeyword,'%')");
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultMediaFilter> {
        private MediaType type;
        private Long ownerAdminId;
        private Long ownerUserId;
        private String prefixKeyword;
        private String likeKeyword;

        public T type(MediaType type) {
            this.type = type;
            return (T) this;
        }

        public T ownerAdminId(Long ownerAdminId) {
            this.ownerAdminId = ownerAdminId;
            return (T) this;
        }

        public T ownerUserId(Long ownerUserId) {
            this.ownerUserId = ownerUserId;
            return (T) this;
        }

        public T prefixKeyword(String prefixKeyword) {
            this.prefixKeyword = prefixKeyword;
            return (T) this;
        }

        public T likeKeyword(String likeKeyword) {
            this.likeKeyword = likeKeyword;
            return (T) this;
        }

        @Override
        public DefaultMediaFilter build() {
            return new DefaultMediaFilter(this);
        }
    }
}
