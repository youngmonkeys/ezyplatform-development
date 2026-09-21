package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountFilter;
import org.youngmonkeys.ezyplatform.pagination.DataRecordCountPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationDataRecordCountRepository extends CommonPaginationRepository<
    DataRecordCountFilter,
    DataRecordCountPaginationParameter,
    Long,
    DataRecordCount> {

    @Override
    protected Class<DataRecordCount> getEntityType() {
        return DataRecordCount.class;
    }
}
