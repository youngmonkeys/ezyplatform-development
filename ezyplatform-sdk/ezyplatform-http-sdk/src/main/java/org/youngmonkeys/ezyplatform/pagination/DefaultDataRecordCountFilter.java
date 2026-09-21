package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezyfox.builder.EzyBuilder;

import java.time.LocalDateTime;
import java.util.Collection;

public class DefaultDataRecordCountFilter implements DataRecordCountFilter {
    public final String dataType;
    public final LocalDateTime lastCountedAtGte;
    public final LocalDateTime lastCountedAtLt;
    public final LocalDateTime lastCountedAtLte;
    public final String dataName;
    public final String recordType;
    public final String queryType;
    public final String parameterType;
    public final Collection<String> keywords;
    public final String keywordPrefix;

    protected DefaultDataRecordCountFilter(Builder<?> builder) {
        this.dataType = builder.dataType;
        this.lastCountedAtGte = builder.lastCountedAtGte;
        this.lastCountedAtLt = builder.lastCountedAtLt;
        this.lastCountedAtLte = builder.lastCountedAtLte;
        this.dataName = builder.dataName;
        this.recordType = builder.recordType;
        this.queryType = builder.queryType;
        this.parameterType = builder.parameterType;
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
        if (dataType != null) {
            answer.and("e.dataType = :dataType");
        }
        if (lastCountedAtGte != null) {
            answer.and("e.lastCountedAt >= :lastCountedAtGte");
        }
        if (lastCountedAtLt != null) {
            answer.and("e.lastCountedAt < :lastCountedAtLt");
        }
        if (lastCountedAtLte != null) {
            answer.and("e.lastCountedAt <= :lastCountedAtLte");
        }
        if (dataName != null) {
            answer.and("e.dataName = :dataName");
        }
        if (recordType != null) {
            answer.and("e.recordType = :recordType");
        }
        if (queryType != null) {
            answer.and("e.queryType = :queryType");
        }
        if (parameterType != null) {
            answer.and("e.parameterType = :parameterType");
        }
        if (keywordPrefix != null || keywords != null) {
            answer.and("k.dataType = 'ezy_data_record_counts'");
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
        implements EzyBuilder<DefaultDataRecordCountFilter> {

        private String dataType;
        private LocalDateTime lastCountedAtGte;
        private LocalDateTime lastCountedAtLt;
        private LocalDateTime lastCountedAtLte;
        private String dataName;
        private String recordType;
        private String queryType;
        private String parameterType;
        private Collection<String> keywords;
        private String keywordPrefix;

        public T dataType(String dataType) {
            this.dataType = dataType;
            return (T) this;
        }

        public T lastCountedAtGte(LocalDateTime lastCountedAtGte) {
            this.lastCountedAtGte = lastCountedAtGte;
            return (T) this;
        }

        public T lastCountedAtLt(LocalDateTime lastCountedAtLt) {
            this.lastCountedAtLt = lastCountedAtLt;
            return (T) this;
        }

        public T lastCountedAtLte(LocalDateTime lastCountedAtLte) {
            this.lastCountedAtLte = lastCountedAtLte;
            return (T) this;
        }

        public T dataName(String dataName) {
            this.dataName = dataName;
            return (T) this;
        }

        public T recordType(String recordType) {
            this.recordType = recordType;
            return (T) this;
        }

        public T queryType(String queryType) {
            this.queryType = queryType;
            return (T) this;
        }

        public T parameterType(String parameterType) {
            this.parameterType = parameterType;
            return (T) this;
        }

        public T keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return (T) this;
        }

        public T keywordPrefix(String keywordPrefix) {
            this.keywordPrefix = keywordPrefix;
            return (T) this;
        }

        @Override
        public DefaultDataRecordCountFilter build() {
            return new DefaultDataRecordCountFilter(this);
        }
    }
}
