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
public class RoleIdDescUserIdDescUserRolePaginationParameter
    implements UserRolePaginationParameter {

    public Long roleId;
    public Long userId;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionDesc(
                nextPage,
                "roleId",
                "userId"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByDesc(
            nextPage,
            "roleId", "userId"
        );
    }

    @Override
    public boolean isEmpty() {
        return isAllNull(roleId, userId);
    }

    @Override
    public String sortOrder() {
        return UserRolePaginationSortOrder
            .ROLE_ID_DESC_USER_ID_DESC
            .toString();
    }
}
