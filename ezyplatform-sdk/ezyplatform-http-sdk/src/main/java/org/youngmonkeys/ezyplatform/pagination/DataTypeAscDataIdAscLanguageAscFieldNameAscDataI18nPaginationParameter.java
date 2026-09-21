package org.youngmonkeys.ezyplatform.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByAsc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionAsc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataTypeAscDataIdAscLanguageAscFieldNameAscDataI18nPaginationParameter
    implements DataI18nPaginationParameter {

    public String dataType;
    public Long dataId;
    public String language;
    public String fieldName;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "dataType",
                "dataId",
                "language",
                "fieldName"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(
            nextPage,
            "dataType", "dataId", "language", "fieldName"
        );
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(dataType, dataId, language, fieldName);
    }

    @Override
    public String sortOrder() {
        return DataI18nPaginationSortOrder
            .DATA_TYPE_ASC_DATA_ID_ASC_LANGUAGE_ASC_FIELD_NAME_ASC
            .toString();
    }
}
