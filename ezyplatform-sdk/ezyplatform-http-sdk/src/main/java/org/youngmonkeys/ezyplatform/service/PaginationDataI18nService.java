package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.EzyplatformDevelopmentEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.pagination.DataI18nFilter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescDataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataI18nRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationDataI18nService extends CommonPaginationService<
    DataI18nModel,
    DataI18nFilter,
    DataI18nPaginationParameter,
    Long,
    DataI18n> {

    private final EzyplatformDevelopmentEntityToModelConverter entityToModelConverter;

    public PaginationDataI18nService(
        PaginationDataI18nRepository repository,
        EzyplatformDevelopmentEntityToModelConverter entityToModelConverter,
        DataI18nPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected DataI18nModel convertEntity(DataI18n entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataI18nPaginationParameter defaultPaginationParameter() {
        return new IdDescDataI18nPaginationParameter();
    }
}
