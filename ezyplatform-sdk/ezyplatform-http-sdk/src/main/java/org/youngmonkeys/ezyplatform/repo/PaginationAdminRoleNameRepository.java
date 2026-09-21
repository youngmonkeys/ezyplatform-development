package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationAdminRoleNameRepository extends CommonPaginationRepository<
    AdminRoleNameFilter,
    AdminRoleNamePaginationParameter,
    Long,
    AdminRoleName> {

    @Override
    protected Class<AdminRoleName> getEntityType() {
        return AdminRoleName.class;
    }
}
