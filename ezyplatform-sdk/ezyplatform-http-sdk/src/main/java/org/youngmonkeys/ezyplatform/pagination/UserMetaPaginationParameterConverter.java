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

import org.youngmonkeys.ezyplatform.model.UserMetaModel;

import java.util.Map;
import java.util.function.Function;

public class UserMetaPaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        UserMetaModel
    > {

    public UserMetaPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            UserMetaPaginationSortOrder.ID_ASC.toString(),
            IdAscUserMetaPaginationParameter.class
        );
        map.put(
            UserMetaPaginationSortOrder.ID_DESC.toString(),
            IdDescUserMetaPaginationParameter.class
        );
        map.put(
            MetadataPaginationSortOrder.META_NUMBER_VALUE_ASC_ID_ASC.toString(),
            MetaNumberValueAscIdAscMetadataPaginationParameter.class
        );
        map.put(
            MetadataPaginationSortOrder.META_NUMBER_VALUE_DESC_ID_DESC.toString(),
            MetaNumberValueDescIdDescMetadataPaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<UserMetaModel, Object>> map
    ) {
        map.put(
            UserMetaPaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscUserMetaPaginationParameter(
                model.getId()
            )
        );
        map.put(
            UserMetaPaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescUserMetaPaginationParameter(
                model.getId()
            )
        );
        map.put(
            MetadataPaginationSortOrder.META_NUMBER_VALUE_ASC_ID_ASC.toString(),
            model -> new MetaNumberValueAscIdAscMetadataPaginationParameter(
                model.getMetaNumberValue(),
                model.getId()
            )
        );
        map.put(
            MetadataPaginationSortOrder.META_NUMBER_VALUE_DESC_ID_DESC.toString(),
            model -> new MetaNumberValueDescIdDescMetadataPaginationParameter(
                model.getMetaNumberValue(),
                model.getId()
            )
        );
    }
}
