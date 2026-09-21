package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.AdminRole;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationAdminRoleRepository extends CommonPaginationRepository<
    AdminRoleFilter,
    AdminRolePaginationParameter,
    Long,
    AdminRole> {

    @Override
    protected Class<AdminRole> getEntityType() {
        return AdminRole.class;
    }
}
