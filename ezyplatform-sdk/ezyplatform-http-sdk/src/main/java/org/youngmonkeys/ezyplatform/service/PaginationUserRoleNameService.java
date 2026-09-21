package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.UserRoleName;
import org.youngmonkeys.ezyplatform.model.UserRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNameFilter;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.UserRoleNamePaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescUserRoleNamePaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationUserRoleNameRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationUserRoleNameService extends CommonPaginationService<
    UserRoleNameModel,
    UserRoleNameFilter,
    UserRoleNamePaginationParameter,
    Long,
    UserRoleName> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationUserRoleNameService(
        PaginationUserRoleNameRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        UserRoleNamePaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected UserRoleNameModel convertEntity(UserRoleName entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected UserRoleNamePaginationParameter defaultPaginationParameter() {
        return new IdDescUserRoleNamePaginationParameter();
    }
}
