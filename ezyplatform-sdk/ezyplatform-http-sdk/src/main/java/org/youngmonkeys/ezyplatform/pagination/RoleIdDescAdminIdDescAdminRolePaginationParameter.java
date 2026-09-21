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
public class RoleIdDescAdminIdDescAdminRolePaginationParameter
    implements AdminRolePaginationParameter {

    public Long roleId;
    public Long adminId;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "roleId",
                "adminId"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(
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
            .ROLE_ID_DESC_ADMIN_ID_DESC
            .toString();
    }
}
