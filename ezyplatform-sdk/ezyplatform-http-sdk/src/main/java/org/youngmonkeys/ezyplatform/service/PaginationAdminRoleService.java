package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AdminRole;
import org.youngmonkeys.ezyplatform.entity.AdminRoleId;
import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.RoleIdDescAdminIdDescAdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRoleRepository;

public class PaginationAdminRoleService extends CommonPaginationService<
    AdminRoleModel,
    AdminRoleFilter,
    AdminRolePaginationParameter,
    AdminRoleId,
    AdminRole> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminRoleService(
        PaginationAdminRoleRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminRolePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected AdminRoleModel convertEntity(AdminRole entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected AdminRolePaginationParameter defaultPaginationParameter() {
        return new RoleIdDescAdminIdDescAdminRolePaginationParameter();
    }
}
