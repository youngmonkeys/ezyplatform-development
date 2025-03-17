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

import org.youngmonkeys.ezyplatform.model.UniqueDataModel;

import java.util.Map;
import java.util.function.Function;

public class UniqueDataPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        UniqueDataModel
    > {

    public UniqueDataPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            UniqueDataPaginationSortOrder
                .TEXT_VALUE_ASC_DATA_ID_ASC
                .toString(),
            TextValueAscDataIdAscUniqueDataPaginationParameter.class
        );
        map.put(
            UniqueDataPaginationSortOrder
                .TEXT_VALUE_DESC_DATA_ID_DESC
                .toString(),
            TextValueDescDataIdDescUniqueDataPaginationParameter.class
        );
        map.put(
            UniqueDataPaginationSortOrder
                .NUMBER_VALUE_ASC_DATA_ID_ASC
                .toString(),
            NumberValueAscDataIdAscUniqueDataPaginationParameter.class
        );
        map.put(
            UniqueDataPaginationSortOrder
                .NUMBER_VALUE_DESC_DATA_ID_DESC
                .toString(),
            NumberValueDescDataIdDescUniqueDataPaginationParameter.class
        );
        map.put(
            UniqueDataPaginationSortOrder
                .DECIMAL_VALUE_ASC_DATA_ID_ASC
                .toString(),
            DecimalValueAscDataIdAscUniqueDataPaginationParameter.class
        );
        map.put(
            UniqueDataPaginationSortOrder
                .DECIMAL_VALUE_DESC_DATA_ID_DESC
                .toString(),
            DecimalValueDescDataIdDescUniqueDataPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<UniqueDataModel, Object>> map
    ) {
        map.put(
            UniqueDataPaginationSortOrder
                .TEXT_VALUE_ASC_DATA_ID_ASC
                .toString(),
            model -> new TextValueAscDataIdAscUniqueDataPaginationParameter(
                model.getTextValue(),
                model.getDataId()
            )
        );
        map.put(
            UniqueDataPaginationSortOrder
                .TEXT_VALUE_DESC_DATA_ID_DESC
                .toString(),
            model -> new TextValueDescDataIdDescUniqueDataPaginationParameter(
                model.getTextValue(),
                model.getDataId()
            )
        );
        map.put(
            UniqueDataPaginationSortOrder
                .NUMBER_VALUE_ASC_DATA_ID_ASC
                .toString(),
            model -> new NumberValueAscDataIdAscUniqueDataPaginationParameter(
                model.getNumberValue(),
                model.getDataId()
            )
        );
        map.put(
            UniqueDataPaginationSortOrder
                .NUMBER_VALUE_DESC_DATA_ID_DESC
                .toString(),
            model -> new NumberValueDescDataIdDescUniqueDataPaginationParameter(
                model.getNumberValue(),
                model.getDataId()
            )
        );
        map.put(
            UniqueDataPaginationSortOrder
                .DECIMAL_VALUE_ASC_DATA_ID_ASC
                .toString(),
            model -> new DecimalValueAscDataIdAscUniqueDataPaginationParameter(
                model.getDecimalValue(),
                model.getDataId()
            )
        );
        map.put(
            UniqueDataPaginationSortOrder
                .NUMBER_VALUE_DESC_DATA_ID_DESC
                .toString(),
            model -> new DecimalValueDescDataIdDescUniqueDataPaginationParameter(
                model.getDecimalValue(),
                model.getDataId()
            )
        );
    }
}
