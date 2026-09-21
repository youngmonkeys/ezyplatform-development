package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.Collection;

public class DefaultAdminRoleNameFilter implements AdminRoleNameFilter {
    public final Collection<String> keywords;
    public final String keywordPrefix;

    protected DefaultAdminRoleNameFilter(Builder<?> builder) {
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
            answer.and("k.dataType = 'ezy_admin_role_names'");
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
        implements EzyBuilder<DefaultAdminRoleNameFilter> {

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
        public DefaultAdminRoleNameFilter build() {
            return new DefaultAdminRoleNameFilter(this);
        }
    }
}
