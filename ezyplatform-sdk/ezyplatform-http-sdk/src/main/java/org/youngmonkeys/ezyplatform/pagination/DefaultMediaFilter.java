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
import org.youngmonkeys.ezyplatform.entity.MediaStatus;
import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.util.Collection;

public class DefaultMediaFilter implements MediaFilter {
    public final MediaType type;
    public final String uploadFrom;
    public final MediaType mediaType;
    public final Collection<MediaType> mediaTypes;
    public final Long ownerAdminId;
    public final Long ownerUserId;
    public final String prefixKeyword;
    public final String likeKeyword;
    public final MediaStatus status;
    public final Collection<MediaStatus> statuses;
    public final MediaStatus exclusiveStatus;
    public final Boolean publicMedia;

    protected DefaultMediaFilter(Builder<?> builder) {
        this.type = builder.type;
        this.uploadFrom = builder.uploadFrom;
        this.mediaType = builder.mediaType;
        this.mediaTypes = builder.mediaTypes;
        this.ownerAdminId = builder.ownerAdminId;
        this.ownerUserId = builder.ownerUserId;
        this.prefixKeyword = builder.prefixKeyword;
        this.likeKeyword = builder.likeKeyword;
        this.status = builder.status;
        this.statuses = builder.statuses;
        this.exclusiveStatus = builder.exclusiveStatus;
        this.publicMedia = builder.publicMedia;
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (type != null) {
            answer.and("e.type = :type");
        }
        if (uploadFrom != null) {
            answer.and("e.uploadFrom = :uploadFrom");
        }
        if (mediaType != null) {
            answer.and("e.type = :mediaType");
        }
        if (mediaTypes != null) {
            answer.and("e.type IN :mediaTypes");
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
        if (status != null) {
            answer.and("e.status = :status");
        }
        if (statuses != null) {
            answer.and("e.status IN :statuses");
        }
        if (exclusiveStatus != null) {
            answer.and("e.status <> :exclusiveStatus");
        }
        if (publicMedia != null) {
            answer.and("e.publicMedia = :publicMedia");
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
        private String uploadFrom;
        private MediaType mediaType;
        private Collection<MediaType> mediaTypes;
        private Long ownerAdminId;
        private Long ownerUserId;
        private String prefixKeyword;
        private String likeKeyword;
        private MediaStatus status;
        private Collection<MediaStatus> statuses;
        private MediaStatus exclusiveStatus;
        private Boolean publicMedia;

        public T type(MediaType type) {
            this.type = type;
            return (T) this;
        }

        public T uploadFrom(String uploadFrom) {
            this.uploadFrom = uploadFrom;
            return (T) this;
        }

        public T mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return (T) this;
        }

        public T mediaTypes(Collection<MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
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

        public T status(MediaStatus status) {
            this.status = status;
            return (T) this;
        }

        public T statuses(Collection<MediaStatus> statuses) {
            this.statuses = statuses;
            return (T) this;
        }

        public T exclusiveStatus(MediaStatus exclusiveStatus) {
            this.exclusiveStatus = exclusiveStatus;
            return (T) this;
        }

        public T publicMedia(Boolean publicMedia) {
            this.publicMedia = publicMedia;
            return (T) this;
        }

        @Override
        public DefaultMediaFilter build() {
            return new DefaultMediaFilter(this);
        }
    }
}
