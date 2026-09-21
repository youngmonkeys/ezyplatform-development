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
        map.put(
            DataRecordCountPaginationSortOrder
                .LAST_COUNTED_AT_ASC_ID_ASC
                .toString(),
            LastCountedAtAscIdAscDataRecordCountPaginationParameter.class
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .LAST_COUNTED_AT_DESC_ID_DESC
                .toString(),
            LastCountedAtDescIdDescDataRecordCountPaginationParameter.class
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .RECORD_COUNT_ASC_ID_ASC
                .toString(),
            RecordCountAscIdAscDataRecordCountPaginationParameter.class
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .RECORD_COUNT_DESC_ID_DESC
                .toString(),
            RecordCountDescIdDescDataRecordCountPaginationParameter.class
        );
    }

    @SuppressWarnings("MethodLength")
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
        map.put(
            DataRecordCountPaginationSortOrder
                .LAST_COUNTED_AT_ASC_ID_ASC
                .toString(),
            model -> new LastCountedAtAscIdAscDataRecordCountPaginationParameter(
                model.getLastCountedAtLocalDateTime(),
                model.getId()
            )
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .LAST_COUNTED_AT_DESC_ID_DESC
                .toString(),
            model -> new LastCountedAtDescIdDescDataRecordCountPaginationParameter(
                model.getLastCountedAtLocalDateTime(),
                model.getId()
            )
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .RECORD_COUNT_ASC_ID_ASC
                .toString(),
            model -> new RecordCountAscIdAscDataRecordCountPaginationParameter(
                model.getRecordCount(),
                model.getId()
            )
        );
        map.put(
            DataRecordCountPaginationSortOrder
                .RECORD_COUNT_DESC_ID_DESC
                .toString(),
            model -> new RecordCountDescIdDescDataRecordCountPaginationParameter(
                model.getRecordCount(),
                model.getId()
            )
        );
    }
}
