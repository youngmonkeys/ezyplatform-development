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
public class RoleIdAscAdminIdAscAdminRolePaginationParameter
    implements AdminRolePaginationParameter {

    public Long roleId;
    public Long adminId;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "roleId",
                "adminId"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(
            nextPage,
            "roleId", "adminId"
        );
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(roleId, adminId);
    }

    @Override
    public String sortOrder() {
        return AdminRolePaginationSortOrder
            .ROLE_ID_ASC_ADMIN_ID_ASC
            .toString();
    }
}
