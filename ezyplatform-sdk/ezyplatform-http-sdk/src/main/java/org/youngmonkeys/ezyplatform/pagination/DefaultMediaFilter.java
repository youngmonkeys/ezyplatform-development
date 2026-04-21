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

import java.time.LocalDateTime;
import java.util.Collection;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;

public class DefaultMediaFilter implements MediaFilter {
    public final String uploadFrom;
    public final String type;
    public final Collection<String> types;
    public final Long ownerAdminId;
    public final Long ownerUserId;
    public final String prefixKeyword;
    public final String likeKeyword;
    public final String status;
    public final Collection<String> statuses;
    public final String exclusiveStatus;
    public final Collection<String> exclusiveStatuses;
    public final Boolean publicMedia;
    public final LocalDateTime createdAtStartInclusive;
    public final LocalDateTime createdAtEndExclusive;
    public final LocalDateTime createdAtEndInclusive;

    protected DefaultMediaFilter(Builder<?> builder) {
        this.type = builder.type;
        this.uploadFrom = builder.uploadFrom;
        this.types = builder.types;
        this.ownerAdminId = builder.ownerAdminId;
        this.ownerUserId = builder.ownerUserId;
        this.prefixKeyword = builder.prefixKeyword;
        this.likeKeyword = builder.likeKeyword;
        this.status = builder.status;
        this.statuses = builder.statuses;
        this.exclusiveStatus = builder.exclusiveStatus;
        this.exclusiveStatuses = builder.exclusiveStatuses;
        this.publicMedia = builder.publicMedia;
        this.createdAtStartInclusive = builder.createdAtStartInclusive;
        this.createdAtEndExclusive = builder.createdAtEndExclusive;
        this.createdAtEndInclusive = builder.createdAtEndInclusive;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (ownerAdminId != null) {
            answer.and("e.ownerAdminId = :ownerAdminId");
        }
        if (ownerUserId != null) {
            answer.and("e.ownerUserId = :ownerUserId");
        }
        if (uploadFrom != null) {
            answer.and("e.uploadFrom = :uploadFrom");
        }
        if (type != null) {
            answer.and("e.type = :type");
        }
        if (types != null) {
            answer.and("e.type IN :types");
        }
        if (publicMedia != null) {
            answer.and("e.publicMedia = :publicMedia");
        }
        if (status != null) {
            answer.and("e.status = :status");
        }
        if (statuses != null) {
            answer.and("e.status IN :statuses");
        }
        if (exclusiveStatus != null) {
            answer.and("e.status <> :exclusiveStatus");
        }
        if (exclusiveStatuses != null) {
            answer.and("e.status NOT IN :exclusiveStatuses");
        }
        if (prefixKeyword != null) {
            answer.and("e.originalName LIKE CONCAT(:prefixKeyword,'%')");
        }
        if (likeKeyword != null) {
            answer.and("e.originalName LIKE CONCAT('%',:likeKeyword,'%')");
        }
        if (this.createdAtStartInclusive != null) {
            answer.and("e.createdAt >= :createdAtStartInclusive");
        }

        if (this.createdAtEndExclusive != null) {
            answer.and("e.createdAt < :createdAtEndExclusive");
        }

        if (this.createdAtEndInclusive != null) {
            answer.and("e.createdAt <= :createdAtEndInclusive");
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultMediaFilter> {
        private String type;
        private String uploadFrom;
        private Collection<String> types;
        private Long ownerAdminId;
        private Long ownerUserId;
        private boolean allowSearchByLikeOperator;
        private String prefixKeyword;
        private String likeKeyword;
        private String status;
        private Collection<String> statuses;
        private String exclusiveStatus;
        private Collection<String> exclusiveStatuses;
        private Boolean publicMedia;
        private LocalDateTime createdAtStartInclusive;
        private LocalDateTime createdAtEndExclusive;
        private LocalDateTime createdAtEndInclusive;

        public T type(String type) {
            this.type = type;
            return (T) this;
        }

        public T uploadFrom(String uploadFrom) {
            this.uploadFrom = uploadFrom;
            return (T) this;
        }

        public T types(Collection<String> types) {
            this.types = types;
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

        public T allowSearchByLikeOperator(
            boolean allowSearchByLikeOperator
        ) {
            this.allowSearchByLikeOperator = allowSearchByLikeOperator;
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

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        public T statuses(Collection<String> statuses) {
            this.statuses = statuses;
            return (T) this;
        }

        public T exclusiveStatus(String exclusiveStatus) {
            this.exclusiveStatus = exclusiveStatus;
            return (T) this;
        }

        public T exclusiveStatuses(Collection<String> exclusiveStatuses) {
            this.exclusiveStatuses = exclusiveStatuses;
            return (T) this;
        }

        public T publicMedia(Boolean publicMedia) {
            this.publicMedia = publicMedia;
            return (T) this;
        }

        public T createdAtStartInclusive(LocalDateTime createdAtStartInclusive) {
            this.createdAtStartInclusive = createdAtStartInclusive;
            return (T) this;
        }

        public T createdAtEndExclusive(LocalDateTime createdAtEndExclusive) {
            this.createdAtEndExclusive = createdAtEndExclusive;
            return (T) this;
        }

        public T createdAtEndInclusive(LocalDateTime createdAtEndInclusive) {
            this.createdAtEndInclusive = createdAtEndInclusive;
            return (T) this;
        }

        @Override
        public DefaultMediaFilter build() {
            if (allowSearchByLikeOperator) {
                prefixKeyword = NULL_STRING;
            } else {
                if (likeKeyword != null) {
                    prefixKeyword = likeKeyword;
                }
                likeKeyword = NULL_STRING;
            }
            return new DefaultMediaFilter(this);
        }
    }
}
