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

import java.util.Collection;

@Getter
public class DefaultUserFilter implements UserFilter {
    public final String status;
    public final Collection<String> statuses;
    public final String likeKeyword;
    public final Collection<String> keywords;
    public final Collection<Long> roleIds;
    public final Collection<String> roleNames;

    protected DefaultUserFilter(Builder<?> builder) {
        this.status = builder.status;
        this.statuses = builder.statuses;
        this.likeKeyword = builder.likeKeyword;
        this.keywords = builder.keywords;
        this.roleIds = builder.roleIds;
        this.roleNames = builder.roleNames;
    }

    @Override
    public void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {
        if (keywords != null) {
            queryString.append(" INNER JOIN UserKeyword k ON e.id = k.userId");
        }
        if (roleIds != null) {
            queryString.append(" INNER JOIN UserRole l ON e.id = l.userId");
        }
        if (roleNames != null) {
            queryString
                .append(" INNER JOIN UserRole l ON e.id = l.userId")
                .append(" INNER JOIN UserRoleName m ON m.id = m.roleId");
        }
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (status != null) {
            answer.and("e.status = :status");
        }
        if (statuses != null) {
            answer.and("e.status in :statuses");
        }
        if (keywords != null) {
            answer.and("k.keyword IN :keywords");
        }
        if (roleIds != null) {
            answer.and("l.roleId IN :roleIds");
        }
        if (roleNames != null) {
            answer.and("m.roleName IN :roleNames");
        }
        if (likeKeyword != null) {
            answer.and(
                new EzyQueryConditionBuilder()
                    .append("(")
                    .append("e.phone LIKE CONCAT('%',:likeKeyword,'%')")
                    .or("e.email LIKE CONCAT('%',:likeKeyword,'%')")
                    .or("e.displayName LIKE CONCAT('%',:likeKeyword,'%')")
                    .or("e.username LIKE CONCAT('%',:likeKeyword,'%')")
                    .or("e.uuid LIKE CONCAT('%',:likeKeyword,'%')")
                    .or("e.id LIKE CONCAT('%',:likeKeyword,'%')")
                    .append(")")
                    .build()
            );
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultUserFilter> {

        private String status;
        private Collection<String> statuses;
        public String likeKeyword;
        private Collection<String> keywords;
        private Collection<Long> roleIds;
        private Collection<String> roleNames;

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        public T statuses(Collection<String> statuses) {
            this.statuses = statuses;
            return (T) this;
        }

        public T likeKeyword(String likeKeyword) {
            this.likeKeyword = likeKeyword;
            return (T) this;
        }

        public T keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return (T) this;
        }

        public T roleIds(Collection<Long> roleIds) {
            this.roleIds = roleIds;
            return (T) this;
        }

        public T roleNames(Collection<String> roleNames) {
            this.roleNames = roleNames;
            return (T) this;
        }

        @Override
        public DefaultUserFilter build() {
            return new DefaultUserFilter(this);
        }
    }
}
