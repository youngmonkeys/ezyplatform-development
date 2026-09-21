package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.EzyplatformDevelopmentEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AdminRole;
import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminRolePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescAdminRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRoleRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationAdminRoleService extends CommonPaginationService<
    AdminRoleModel,
    AdminRoleFilter,
    AdminRolePaginationParameter,
    Long,
    AdminRole> {

    private final EzyplatformDevelopmentEntityToModelConverter entityToModelConverter;

    public PaginationAdminRoleService(
        PaginationAdminRoleRepository repository,
        EzyplatformDevelopmentEntityToModelConverter entityToModelConverter,
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
        return new IdDescAdminRolePaginationParameter();
    }
}
