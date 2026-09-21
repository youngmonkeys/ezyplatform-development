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
            AdminRolePaginationSortOrder
                .ROLE_ID_ASC_ADMIN_ID_ASC
                .toString(),
            RoleIdAscAdminIdAscAdminRolePaginationParameter.class
        );
        map.put(
            AdminRolePaginationSortOrder
                .ROLE_ID_DESC_ADMIN_ID_DESC
                .toString(),
            RoleIdDescAdminIdDescAdminRolePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<AdminRoleModel, Object>> map
    ) {
        map.put(
            AdminRolePaginationSortOrder
                .ROLE_ID_ASC_ADMIN_ID_ASC
                .toString(),
            model -> new RoleIdAscAdminIdAscAdminRolePaginationParameter(
                model.getRoleId(),
                model.getAdminId()
            )
        );
        map.put(
            AdminRolePaginationSortOrder
                .ROLE_ID_DESC_ADMIN_ID_DESC
                .toString(),
            model -> new RoleIdDescAdminIdDescAdminRolePaginationParameter(
                model.getRoleId(),
                model.getAdminId()
            )
        );
    }
}
