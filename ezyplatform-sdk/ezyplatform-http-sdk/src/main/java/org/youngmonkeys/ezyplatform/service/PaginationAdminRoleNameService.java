package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.model.AdminRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.AdminRoleNamePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescAdminRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationAdminRoleNameRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationAdminRoleNameService extends CommonPaginationService<
    AdminRoleNameModel,
    AdminRoleNameFilter,
    AdminRoleNamePaginationParameter,
    Long,
    AdminRoleName> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationAdminRoleNameService(
        PaginationAdminRoleNameRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        AdminRoleNamePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected AdminRoleNameModel convertEntity(AdminRoleName entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected AdminRoleNamePaginationParameter defaultPaginationParameter() {
        return new IdDescAdminRoleNamePaginationParameter();
    }
}
