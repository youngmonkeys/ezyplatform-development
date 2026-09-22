/*
 * Copyright 2026 youngmonkeys.org
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

public class DefaultAdminRoleFilter implements AdminRoleFilter {
    public final Collection<String> keywords;
    public final String keywordPrefix;

    protected DefaultAdminRoleFilter(Builder<?> builder) {
        this.keywords = builder.keywords;
        this.keywordPrefix = builder.keywordPrefix;
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
        if (keywordPrefix != null || keywords != null) {
            answer.and("k.dataType = 'ezy_admin_roles'");
            if (keywordPrefix != null) {
                answer.and("k.keyword LIKE CONCAT(:keywordPrefix, '%')");
            }
            if (keywords != null) {
                answer.and("k.keyword IN :keywords");
            }
        }
        return answer.build();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultAdminRoleFilter> {

        private Collection<String> keywords;
        private String keywordPrefix;

        public T keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return (T) this;
        }

        public T keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return (T) this;
        }

        @Override
        public DefaultAdminRoleFilter build() {
            return new DefaultAdminRoleFilter(this);
        }
    }
}
