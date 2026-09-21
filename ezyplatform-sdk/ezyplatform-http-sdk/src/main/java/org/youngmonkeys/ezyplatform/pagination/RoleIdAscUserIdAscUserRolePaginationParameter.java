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
public class RoleIdAscUserIdAscUserRolePaginationParameter
    implements UserRolePaginationParameter {

    public Long roleId;
    public Long userId;

    @Override
    public String paginationCondition(boolean nextPage) {
        return isEmpty()
            ? null
            : makePaginationConditionAsc(
                nextPage,
                "roleId",
                "userId"
            );
    }

    @Override
    public String orderBy(boolean nextPage) {
        return makeOrderByAsc(
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
            .ROLE_ID_ASC_USER_ID_ASC
            .toString();
    }
}
