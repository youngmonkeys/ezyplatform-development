package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.converter.EzyplatformDevelopmentEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.model.DataRecordCountModel;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountFilter;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.IdDescDataRecordCountPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationDataRecordCountRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

public class PaginationDataRecordCountService extends CommonPaginationService<
    DataRecordCountModel,
    DataRecordCountFilter,
    DataRecordCountPaginationParameter,
    Long,
    DataRecordCount> {

    private final EzyplatformDevelopmentEntityToModelConverter entityToModelConverter;

    public PaginationDataRecordCountService(
        PaginationDataRecordCountRepository repository,
        EzyplatformDevelopmentEntityToModelConverter entityToModelConverter,
        DataRecordCountPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }


    @Override
    protected DataRecordCountModel convertEntity(DataRecordCount entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected DataRecordCountPaginationParameter defaultPaginationParameter() {
        return new IdDescDataRecordCountPaginationParameter();
    }
}
