package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class AdminRolePaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        AdminRoleModel
    > {

    public AdminRolePaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            AdminRolePaginationSortOrder.ID_ASC.toString(),
            IdAscAdminRolePaginationParameter.class
        );
        map.put(
            AdminRolePaginationSortOrder.ID_DESC.toString(),
            IdDescAdminRolePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<AdminRoleModel, Object>> map
    ) {
        map.put(
            AdminRolePaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscAdminRolePaginationParameter(
                model.getId()
            )
        );
        map.put(
            AdminRolePaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescAdminRolePaginationParameter(
                model.getId()
            )
        );
    }
}
