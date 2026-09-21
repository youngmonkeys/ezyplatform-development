package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class DataI18nPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        DataI18nModel
    > {

    public DataI18nPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            DataI18nPaginationSortOrder.ID_ASC.toString(),
            IdAscDataI18nPaginationParameter.class
        );
        map.put(
            DataI18nPaginationSortOrder.ID_DESC.toString(),
            IdDescDataI18nPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<DataI18nModel, Object>> map
    ) {
        map.put(
            DataI18nPaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscDataI18nPaginationParameter(
                model.getId()
            )
        );
        map.put(
            DataI18nPaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescDataI18nPaginationParameter(
                model.getId()
            )
        );
    }
}
