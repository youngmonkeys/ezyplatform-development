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

public class DefaultDataMappingFilter implements DataMappingFilter {
    public final String mappingName;
    public final Long fromDataId;
    public final Long toDataId;
    public final Long fromOrToDataId;
    public final String textData;
    public final LocalDateTime mappedAtGte;
    public final LocalDateTime mappedAtLt;
    public final LocalDateTime mappedAtLte;
    public final transient String fromInnerJoinEntityName;
    public final transient String fromInnerJoinOnFieldName;
    public final transient String toInnerJoinEntityName;
    public final transient String toInnerJoinOnFieldName;

    protected DefaultDataMappingFilter(Builder builder) {
        this.mappingName = builder.mappingName;
        this.fromDataId = builder.fromDataId;
        this.toDataId = builder.toDataId;
        this.fromOrToDataId = builder.fromOrToDataId;
        this.textData = builder.textData;
        this.mappedAtGte = builder.mappedAtGte;
        this.mappedAtLt = builder.mappedAtLt;
        this.mappedAtLte = builder.mappedAtLte;
        this.fromInnerJoinEntityName = builder.fromInnerJoinEntityName;
        this.fromInnerJoinOnFieldName = builder.fromInnerJoinOnFieldName;
        this.toInnerJoinEntityName = builder.toInnerJoinEntityName;
        this.toInnerJoinOnFieldName = builder.toInnerJoinOnFieldName;
    }

    @Override
    public void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {
        if (fromInnerJoinEntityName != null) {
            queryString.append(" INNER JOIN ")
                .append(fromInnerJoinEntityName)
                .append(" f ON e.fromDataId = f.")
                .append(fromInnerJoinOnFieldName);
        }
        if (toInnerJoinEntityName != null) {
            queryString.append(" INNER JOIN ")
                .append(toInnerJoinEntityName)
                .append(" t ON e.toDataId = t.")
                .append(toInnerJoinOnFieldName);
        }
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (mappingName != null) {
            answer.and("e.mappingName = :mappingName");
        }
        if (fromDataId != null) {
            answer.and("e.fromDataId = :fromDataId");
        }
        if (toDataId != null) {
            answer.and("e.toDataId = :toDataId");
        }
        if (fromOrToDataId != null) {
            answer.and(
                "(e.fromDataId = :fromOrToDataId " +
                    "OR e.toDataId = :fromOrToDataId)"
            );
        }
        if (textData != null) {
            answer.and("e.textData = :textData");
        }
        if (mappedAtGte != null) {
            answer.and("e.mappedAt >= :mappedAtGte");
        }
        if (mappedAtLt != null) {
            answer.and("e.mappedAt < :mappedAtLt");
        }
        if (mappedAtLte != null) {
            answer.and("e.mappedAt <= :mappedAtLte");
        }
        return answer.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<DefaultDataMappingFilter> {
        protected String mappingName;
        protected Long fromDataId;
        protected Long toDataId;
        protected Long fromOrToDataId;
        protected String textData;
        protected LocalDateTime mappedAtGte;
        protected LocalDateTime mappedAtLt;
        protected LocalDateTime mappedAtLte;
        protected String fromInnerJoinEntityName;
        protected String fromInnerJoinOnFieldName;
        protected String toInnerJoinEntityName;
        protected String toInnerJoinOnFieldName;

        public Builder mappingName(String mappingName) {
            this.mappingName = mappingName;
            return this;
        }

        public Builder fromDataId(Long fromDataId) {
            this.fromDataId = fromDataId;
            return this;
        }

        public Builder toDataId(Long toDataId) {
            this.toDataId = toDataId;
            return this;
        }

        public Builder fromOrToDataId(Long fromOrToDataId) {
            this.fromOrToDataId = fromOrToDataId;
            return this;
        }

        public Builder textData(String textData) {
            this.textData = textData;
            return this;
        }

        public Builder mappedAtGte(LocalDateTime mappedAtGte) {
            this.mappedAtGte = mappedAtGte;
            return this;
        }

        public Builder mappedAtLt(LocalDateTime mappedAtLt) {
            this.mappedAtLt = mappedAtLt;
            return this;
        }

        public Builder mappedAtLte(LocalDateTime mappedAtLte) {
            this.mappedAtLte = mappedAtLte;
            return this;
        }

        public Builder fromInnerJoinEntityName(String fromInnerJoinEntityName) {
            this.fromInnerJoinEntityName = fromInnerJoinEntityName;
            return this;
        }

        public Builder fromInnerJoinOnFieldName(String fromInnerJoinOnFieldName) {
            this.fromInnerJoinOnFieldName = fromInnerJoinOnFieldName;
            return this;
        }

        public Builder toInnerJoinEntityName(String toInnerJoinEntityName) {
            this.toInnerJoinEntityName = toInnerJoinEntityName;
            return this;
        }

        public Builder toInnerJoinOnFieldName(String toInnerJoinOnFieldName) {
            this.toInnerJoinOnFieldName = toInnerJoinOnFieldName;
            return this;
        }

        @Override
        public DefaultDataMappingFilter build() {
            return new DefaultDataMappingFilter(this);
        }
    }
}
