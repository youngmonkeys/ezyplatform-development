package org.youngmonkeys.ezyplatform.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByAsc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionAsc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LastCountedAtAscIdAscDataRecordCountPaginationParameter
    implements DataRecordCountPaginationParameter {

    public LocalDateTime lastCountedAt;
    public Long id;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "lastCountedAt",
                "id"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(nextPage, "lastCountedAt", "id");
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(lastCountedAt, id);
    }

    @Override
    public String sortOrder() {
        return DataRecordCountPaginationSortOrder
            .LAST_COUNTED_AT_ASC_ID_ASC
            .toString();
    }
}
