package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.UserRoleModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class UserRolePaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        UserRoleModel
    > {

    public UserRolePaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            UserRolePaginationSortOrder
                .ROLE_ID_ASC_USER_ID_ASC
                .toString(),
            RoleIdAscUserIdAscUserRolePaginationParameter.class
        );
        map.put(
            UserRolePaginationSortOrder
                .ROLE_ID_DESC_USER_ID_DESC
                .toString(),
            RoleIdDescUserIdDescUserRolePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<UserRoleModel, Object>> map
    ) {
        map.put(
            UserRolePaginationSortOrder
                .ROLE_ID_ASC_USER_ID_ASC
                .toString(),
            model -> new RoleIdAscUserIdAscUserRolePaginationParameter(
                model.getRoleId(),
                model.getUserId()
            )
        );
        map.put(
            UserRolePaginationSortOrder
                .ROLE_ID_DESC_USER_ID_DESC
                .toString(),
            model -> new RoleIdDescUserIdDescUserRolePaginationParameter(
                model.getRoleId(),
                model.getUserId()
            )
        );
    }
}
