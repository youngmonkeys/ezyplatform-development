/*
 * Copyright 2023 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.DataMappingModel;

import java.util.Map;
import java.util.function.Function;

public class DataMappingPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        DataMappingModel
    > {

    public DataMappingPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_ASC_FROM_DATA_ID_ASC
                .toString(),
            DisplayOrderAscFromDataIdAscDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_DESC_FROM_DATA_ID_DESC
                .toString(),
            DisplayOrderDescFromDataIdDescDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_ASC_TO_DATA_ID_ASC
                .toString(),
            DisplayOrderAscToDataIdAscDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_DESC_TO_DATA_ID_DESC
                .toString(),
            DisplayOrderDescToDataIdDescDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_ASC_FROM_DATA_ID_ASC
                .toString(),
            MappedAtAscFromDataIdAscDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_DESC_FROM_DATA_ID_DESC
                .toString(),
            MappedAtDescFromDataIdDescDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_ASC_TO_DATA_ID_ASC
                .toString(),
            MappedAtAscToDataIdAscDataMappingPaginationParameter.class
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_DESC_TO_DATA_ID_DESC
                .toString(),
            MappedAtDescToDataIdDescDataMappingPaginationParameter.class
        );
    }

    @SuppressWarnings("MethodLength")
    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<DataMappingModel, Object>> map
    ) {
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_ASC_FROM_DATA_ID_ASC
                .toString(),
            model -> new DisplayOrderAscFromDataIdAscDataMappingPaginationParameter(
                model.getDisplayOrder(),
                model.getFromDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_DESC_FROM_DATA_ID_DESC
                .toString(),
            model -> new DisplayOrderDescFromDataIdDescDataMappingPaginationParameter(
                model.getDisplayOrder(),
                model.getFromDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_ASC_TO_DATA_ID_ASC
                .toString(),
            model -> new DisplayOrderAscToDataIdAscDataMappingPaginationParameter(
                model.getDisplayOrder(),
                model.getToDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .DISPLAY_ORDER_DESC_TO_DATA_ID_DESC
                .toString(),
            model -> new DisplayOrderDescToDataIdDescDataMappingPaginationParameter(
                model.getDisplayOrder(),
                model.getToDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_ASC_FROM_DATA_ID_ASC
                .toString(),
            model -> new MappedAtAscFromDataIdAscDataMappingPaginationParameter(
                model.getMappedAtLocalDataTime(),
                model.getFromDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_DESC_FROM_DATA_ID_DESC
                .toString(),
            model -> new MappedAtDescFromDataIdDescDataMappingPaginationParameter(
                model.getMappedAtLocalDataTime(),
                model.getFromDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_ASC_TO_DATA_ID_ASC
                .toString(),
            model -> new MappedAtAscToDataIdAscDataMappingPaginationParameter(
                model.getMappedAtLocalDataTime(),
                model.getToDataId()
            )
        );
        map.put(
            DataMappingPaginationSortOrder
                .MAPPED_AT_DESC_TO_DATA_ID_DESC
                .toString(),
            model -> new MappedAtDescToDataIdDescDataMappingPaginationParameter(
                model.getMappedAtLocalDataTime(),
                model.getToDataId()
            )
        );
    }
}
