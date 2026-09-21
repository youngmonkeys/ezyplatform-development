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
public class RecordCountAscIdAscDataRecordCountPaginationParameter
    implements DataRecordCountPaginationParameter {

    public Long recordCount;
    public Long id;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "recordCount",
                "id"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(nextPage, "recordCount", "id");
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(recordCount, id);
    }

    @Override
    public String sortOrder() {
        return DataRecordCountPaginationSortOrder
            .RECORD_COUNT_ASC_ID_ASC
            .toString();
    }
}
