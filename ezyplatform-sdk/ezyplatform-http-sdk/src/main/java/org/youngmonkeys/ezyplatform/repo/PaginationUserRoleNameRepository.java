package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.UserRoleName;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationUserRoleNameRepository extends CommonPaginationRepository<
    UserRoleNameFilter,
    UserRoleNamePaginationParameter,
    Long,
    UserRoleName> {

    @Override
    protected Class<UserRoleName> getEntityType() {
        return UserRoleName.class;
    }
}
