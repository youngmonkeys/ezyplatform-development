/*
 * Copyright 2025 youngmonkeys.org
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

import org.youngmonkeys.ezyplatform.model.ContentTemplateModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class ContentTemplatePaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        ContentTemplateModel
    > {

    public ContentTemplatePaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            ContentTemplatePaginationSortOrder.ID_ASC.toString(),
            IdAscContentTemplatePaginationParameter.class
        );
        map.put(
            ContentTemplatePaginationSortOrder.ID_DESC.toString(),
            IdDescContentTemplatePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<ContentTemplateModel, Object>> map
    ) {
        map.put(
            ContentTemplatePaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscContentTemplatePaginationParameter(
                model.getId()
            )
        );
        map.put(
            ContentTemplatePaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescContentTemplatePaginationParameter(
                model.getId()
            )
        );
    }
}
