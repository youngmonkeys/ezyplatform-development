package org.youngmonkeys.ezyplatform.repo;

import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.entity.DataI18nId;
import org.youngmonkeys.ezyplatform.pagination.DataI18nFilter;
import org.youngmonkeys.ezyplatform.pagination.DataI18nPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.CommonPaginationRepository;

public class PaginationDataI18nRepository extends CommonPaginationRepository<
    DataI18nFilter,
    DataI18nPaginationParameter,
    DataI18nId,
    DataI18n> {

    @Override
    protected Class<DataI18n> getEntityType() {
        return DataI18n.class;
    }
}
