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
public class RecordCountDescIdDescDataRecordCountPaginationParameter
    implements DataRecordCountPaginationParameter {

    public Long recordCount;
    public Long id;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "recordCount",
                "id"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(nextPage, "recordCount", "id");
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(recordCount, id);
    }

    @Override
    public String sortOrder() {
        return DataRecordCountPaginationSortOrder
            .RECORD_COUNT_DESC_ID_DESC
            .toString();
    }
}
