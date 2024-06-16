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

import org.youngmonkeys.ezyplatform.model.SimpleLetterModel;

import java.util.Map;
import java.util.function.Function;

public class LetterPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        SimpleLetterModel
    > {

    public LetterPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            LetterPaginationSortOrder.ID_DESC.toString(),
            IdDescLetterPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<SimpleLetterModel, Object>> map
    ) {
        map.put(
            LetterPaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescLetterPaginationParameter(
                model.getId()
            )
        );
    }
}
