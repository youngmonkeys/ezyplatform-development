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
            UserRolePaginationSortOrder.ID_ASC.toString(),
            IdAscUserRolePaginationParameter.class
        );
        map.put(
            UserRolePaginationSortOrder.ID_DESC.toString(),
            IdDescUserRolePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<UserRoleModel, Object>> map
    ) {
        map.put(
            UserRolePaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscUserRolePaginationParameter(
                model.getId()
            )
        );
        map.put(
            UserRolePaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescUserRolePaginationParameter(
                model.getId()
            )
        );
    }
}
