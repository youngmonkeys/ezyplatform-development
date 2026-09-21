package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.entity.UserRoleId;
import org.youngmonkeys.ezyplatform.pagination.UserRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationUserRoleRepository extends CommonPaginationRepository<
    UserRoleFilter,
    UserRolePaginationParameter,
    UserRoleId,
    UserRole> {

    @Override
    protected Class<UserRole> getEntityType() {
        return UserRole.class;
    }
}
