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

import org.youngmonkeys.ezyplatform.model.CommonSettingModel;

import java.util.Map;
import java.util.function.Function;

public class SettingPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        CommonSettingModel
    > {

    public SettingPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            SettingPaginationSortOrder.ID_DESC.toString(),
            IdDescSettingPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<CommonSettingModel, Object>> map
    ) {
        map.put(
            SettingPaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescSettingPaginationParameter(
                model.getId()
            )
        );
    }
}
