package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.DataRecordCountModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class DataRecordCountPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        DataRecordCountModel
    > {

    public DataRecordCountPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            DataRecordCountPaginationSortOrder.ID_ASC.toString(),
            IdAscDataRecordCountPaginationParameter.class
        );
        map.put(
            DataRecordCountPaginationSortOrder.ID_DESC.toString(),
            IdDescDataRecordCountPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<DataRecordCountModel, Object>> map
    ) {
        map.put(
            DataRecordCountPaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscDataRecordCountPaginationParameter(
                model.getId()
            )
        );
        map.put(
            DataRecordCountPaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescDataRecordCountPaginationParameter(
                model.getId()
            )
        );
    }
}
