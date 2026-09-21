package org.youngmonkeys.ezyplatform.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makeOrderByDesc;
import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.makePaginationConditionDesc;
import static org.youngmonkeys.ezyplatform.util.Values.isAllNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LastCountedAtDescIdDescDataRecordCountPaginationParameter
    implements DataRecordCountPaginationParameter {

    public LocalDateTime lastCountedAt;
    public Long id;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "lastCountedAt",
                "id"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(nextPage, "lastCountedAt", "id");
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(lastCountedAt, id);
    }

    @Override
    public String sortOrder() {
        return DataRecordCountPaginationSortOrder
            .LAST_COUNTED_AT_DESC_ID_DESC
            .toString();
    }
}
