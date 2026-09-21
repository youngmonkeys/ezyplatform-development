package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.entity.UserRoleId;
import org.youngmonkeys.ezyplatform.model.UserRoleModel;
import org.youngmonkeys.ezyplatform.pagination.UserRoleFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRolePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserRolePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.RoleIdDescUserIdDescUserRolePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserRoleRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationUserRoleService extends CommonPaginationService<
    UserRoleModel,
    UserRoleFilter,
    UserRolePaginationParameter,
    UserRoleId,
    UserRole> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserRoleService(
        PaginationUserRoleRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserRolePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected UserRoleModel convertEntity(UserRole entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserRolePaginationParameter defaultPaginationParameter() {
        return new RoleIdDescUserIdDescUserRolePaginationParameter();
    }
}
