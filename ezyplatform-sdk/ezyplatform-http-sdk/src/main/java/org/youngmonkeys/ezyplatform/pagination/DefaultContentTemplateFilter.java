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

public class DefaultContentTemplateFilter implements ContentTemplateFilter {
    public final String ownerType;
    public final Long ownerId;
    public final String templateType;
    public final String templateName;
    public final String contentType;
    public final Long creatorId;
    public final String status;
    public final String keywordPrefix;
    public final Collection<String> keywords;
    public final String likeKeyword;

    protected DefaultContentTemplateFilter(Builder builder) {
        this.ownerType = builder.ownerType;
        this.ownerId = builder.ownerId;
        this.templateType = builder.templateType;
        this.templateName = builder.templateName;
        this.contentType = builder.contentType;
        this.creatorId = builder.creatorId;
        this.status = builder.status;
        this.keywordPrefix = builder.keywordPrefix;
        this.keywords = builder.keywords;
        this.likeKeyword = builder.likeKeyword;
    }

    @Override
    public void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {
        if (keywordPrefix != null || keywords != null) {
            queryString.append(" INNER JOIN DataIndex k ON e.id = k.dataId");
        }
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (ownerType != null) {
            answer.and("e.ownerType = :ownerType");
        }
        if (ownerId != null) {
            answer.and("e.ownerId = :ownerId");
        }
        if (templateType != null) {
            answer.and("e.templateType = :templateType");
        }
        if (templateName != null) {
            answer.and("e.templateName = :templateName");
        }
        if (contentType != null) {
            answer.and("e.contentType = :contentType");
        }
        if (creatorId != null) {
            answer.and("e.creatorId = :creatorId");
        }
        if (status != null) {
            answer.and("e.status = :status");
        }
        if (keywordPrefix != null || keywords != null) {
            answer.and("k.dataType = 'ezy_content_templates'");
        }
        if (keywordPrefix != null) {
            answer.and("k.keyword LIKE CONCAT(:keywordPrefix,'%')");
        }
        if (keywords != null) {
            answer.and("k.keyword IN :keywords");
        }
        if (likeKeyword != null) {
            answer.and(
                "(e.templateName LIKE CONCAT('%',:likeKeyword,'%') " +
                    "OR e.titleTemplate LIKE CONCAT('%',:likeKeyword,'%') " +
                    "OR e.contentTemplate LIKE CONCAT('%',:likeKeyword,'%'))"
            );
        }
        return answer.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultContentTemplateFilter> {
        protected String ownerType;
        protected Long ownerId;
        protected String templateType;
        protected String templateName;
        protected String contentType;
        protected Long creatorId;
        protected String status;
        protected String keywordPrefix;
        protected Collection<String> keywords;
        protected String likeKeyword;

        public Builder ownerType(String ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder templateType(String templateType) {
            this.templateType = templateType;
            return this;
        }

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder creatorId(Long creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return this;
        }

        public Builder keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder likeKeyword(String likeKeyword) {
            this.likeKeyword = likeKeyword;
            return this;
        }

        @Override
        public DefaultContentTemplateFilter build() {
            return new DefaultContentTemplateFilter(this);
        }
    }
}
