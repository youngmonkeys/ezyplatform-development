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
    }
}
