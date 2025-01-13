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
    public final String uniqueKeyword;
    public final String likeKeyword;
    public final Collection<String> keywords;
    public final Long roleId;
    public final Collection<Long> roleIds;
    public final String roleName;
    public final Collection<String> roleNames;
    public final Long exclusiveRoleId;
    public final Collection<Long> exclusiveRoleIds;
    public final String exclusiveRoleName;
    public final Collection<String> exclusiveRoleNames;

    protected DefaultUserFilter(Builder<?> builder) {
        this.status = builder.status;
        this.statuses = builder.statuses;
        this.uniqueKeyword = builder.uniqueKeyword;
        this.likeKeyword = builder.likeKeyword;
        this.keywords = builder.keywords;
        this.roleId = builder.roleId;
        this.roleIds = builder.roleIds;
        this.roleName = builder.roleName;
        this.roleNames = builder.roleNames;
        this.exclusiveRoleId = builder.exclusiveRoleId;
        this.exclusiveRoleIds = builder.exclusiveRoleIds;
        this.exclusiveRoleName = builder.exclusiveRoleName;
        this.exclusiveRoleNames = builder.exclusiveRoleNames;
    }

    @Override
    public void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {
        if (keywords != null) {
            queryString.append(" INNER JOIN UserKeyword k ON e.id = k.userId");
        }
        if (roleId != null
            || roleIds != null
            || roleName != null
            || roleNames != null
        ) {
            queryString.append(" INNER JOIN UserRole l ON e.id = l.userId");
            if (roleName != null
                || roleNames != null
            ) {
                queryString.append(" INNER JOIN UserRoleName m ON m.id = l.roleId");
            }
        }
        if (exclusiveRoleId != null
            || exclusiveRoleName != null
            || exclusiveRoleIds != null
            || exclusiveRoleNames != null
        ) {
            queryString.append(" LEFT JOIN UserRole l ON e.id = l.userId");
            if (exclusiveRoleId != null) {
                queryString.append(" AND l.roleId = :exclusiveRoleId");
            }
            if (exclusiveRoleIds != null) {
                queryString.append(" AND l.roleId IN :exclusiveRoleIds");
            }
            if (exclusiveRoleName != null
                || exclusiveRoleNames != null
            ) {
                queryString.append(" LEFT JOIN UserRoleName m ON m.id = l.roleId");
                if (exclusiveRoleName != null) {
                    queryString.append(" AND m.name = :exclusiveRoleName");
                }
                if (exclusiveRoleNames != null) {
                    queryString.append(" AND m.name IN :exclusiveRoleNames");
                }
            }
        }
    }

    @SuppressWarnings("MethodLength")
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
        if (roleId != null) {
            answer.and("l.roleId = :roleId");
        }
        if (roleIds != null) {
            answer.and("l.roleId IN :roleIds");
        }
        if (roleName != null) {
            answer.and("m.name = :roleName");
        }
        if (roleNames != null) {
            answer.and("m.name IN :roleNames");
        }
        if (exclusiveRoleId != null || exclusiveRoleIds != null) {
            answer.and("l.roleId IS NULL");
        }
        if (exclusiveRoleName != null || exclusiveRoleNames != null) {
            answer.and("m.id IS NULL");
        }
        if (uniqueKeyword != null) {
            answer.and(
                new EzyQueryConditionBuilder()
                    .append("(")
                    .append("e.username = :uniqueKeyword")
                    .or("e.phone = :uniqueKeyword")
                    .or("e.email = :uniqueKeyword")
                    .or("e.uuid = :uniqueKeyword")
                    .or("e.id = :uniqueKeyword")
                    .append(")")
                    .build()
            );
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
        private String uniqueKeyword;
        private String likeKeyword;
        private Collection<String> keywords;
        private Long roleId;
        private Collection<Long> roleIds;
        private String roleName;
        private Collection<String> roleNames;
        private Long exclusiveRoleId;
        private Collection<Long> exclusiveRoleIds;
        private String exclusiveRoleName;
        private Collection<String> exclusiveRoleNames;

        public T status(String status) {
            this.status = status;
            return (T) this;
        }

        public T statuses(Collection<String> statuses) {
            this.statuses = statuses;
            return (T) this;
        }

        public T uniqueKeyword(String uniqueKeyword) {
            this.uniqueKeyword = uniqueKeyword;
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

        public T roleId(Long roleId) {
            this.roleId = roleId;
            return (T) this;
        }

        public T roleIds(Collection<Long> roleIds) {
            this.roleIds = roleIds;
            return (T) this;
        }

        public T roleName(String roleName) {
            this.roleName = roleName;
            return (T) this;
        }

        public T roleNames(Collection<String> roleNames) {
            this.roleNames = roleNames;
            return (T) this;
        }

        public T exclusiveRoleId(Long exclusiveRoleId) {
            this.exclusiveRoleId = exclusiveRoleId;
            return (T) this;
        }

        public T exclusiveRoleIds(Collection<Long> exclusiveRoleIds) {
            this.exclusiveRoleIds = exclusiveRoleIds;
            return (T) this;
        }

        public T exclusiveRoleName(String exclusiveRoleName) {
            this.exclusiveRoleName = exclusiveRoleName;
            return (T) this;
        }

        public T exclusiveRoleNames(Collection<String> exclusiveRoleNames) {
            this.exclusiveRoleNames = exclusiveRoleNames;
            return (T) this;
        }

        @Override
        public DefaultUserFilter build() {
            return new DefaultUserFilter(this);
        }
    }
}
