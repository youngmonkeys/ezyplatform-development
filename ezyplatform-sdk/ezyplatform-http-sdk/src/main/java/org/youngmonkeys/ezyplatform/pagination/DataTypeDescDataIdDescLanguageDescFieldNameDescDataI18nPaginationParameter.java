package org.youngmonkeys.ezyplatform.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByDesc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionDesc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataTypeDescDataIdDescLanguageDescFieldNameDescDataI18nPaginationParameter
    implements DataI18nPaginationParameter {

    public String dataType;
    public Long dataId;
    public String language;
    public String fieldName;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "dataType",
                "dataId",
                "language",
                "fieldName"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(
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
            .DATA_TYPE_DESC_DATA_ID_DESC_LANGUAGE_DESC_FIELD_NAME_DESC
            .toString();
    }
}
