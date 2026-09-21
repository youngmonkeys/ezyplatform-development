/*
 * Copyright 2026 youngmonkeys.org
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

import org.youngmonkeys.ezyplatform.model.AdminRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class AdminRoleNamePaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        AdminRoleNameModel
    > {

    public AdminRoleNamePaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            AdminRoleNamePaginationSortOrder.ID_ASC.toString(),
            IdAscAdminRoleNamePaginationParameter.class
        );
        map.put(
            AdminRoleNamePaginationSortOrder.ID_DESC.toString(),
            IdDescAdminRoleNamePaginationParameter.class
        );
        map.put(
            AdminRoleNamePaginationSortOrder.PRIORITY_ASC_ID_ASC.toString(),
            PriorityAscIdAscAdminRoleNamePaginationParameter.class
        );
        map.put(
            AdminRoleNamePaginationSortOrder.PRIORITY_DESC_ID_DESC.toString(),
            PriorityDescIdDescAdminRoleNamePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<AdminRoleNameModel, Object>> map
    ) {
        map.put(
            AdminRoleNamePaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscAdminRoleNamePaginationParameter(
                model.getId()
            )
        );
        map.put(
            AdminRoleNamePaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescAdminRoleNamePaginationParameter(
                model.getId()
            )
        );
        map.put(
            AdminRoleNamePaginationSortOrder.PRIORITY_ASC_ID_ASC.toString(),
            model -> new PriorityAscIdAscAdminRoleNamePaginationParameter(
                model.getPriority(),
                model.getId()
            )
        );
        map.put(
            AdminRoleNamePaginationSortOrder.PRIORITY_DESC_ID_DESC.toString(),
            model -> new PriorityDescIdDescAdminRoleNamePaginationParameter(
                model.getPriority(),
                model.getId()
            )
        );
    }
}
