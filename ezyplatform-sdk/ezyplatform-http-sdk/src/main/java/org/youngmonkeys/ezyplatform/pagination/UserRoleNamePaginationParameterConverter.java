package org.youngmonkeys.ezyplatform.pagination;

import org.youngmonkeys.ezyplatform.model.UserRoleNameModel;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Map;
import java.util.function.Function;

public class UserRoleNamePaginationParameterConverter
    extends ComplexPaginationParameterConverter<
        String,
        UserRoleNameModel
    > {

    public UserRoleNamePaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        super(converter);
    }

    @Override
    protected void mapPaginationParametersToTypes(
        Map<String, Class<?>> map
    ) {
        map.put(
            UserRoleNamePaginationSortOrder.ID_ASC.toString(),
            IdAscUserRoleNamePaginationParameter.class
        );
        map.put(
            UserRoleNamePaginationSortOrder.ID_DESC.toString(),
            IdDescUserRoleNamePaginationParameter.class
        );
    }

    @Override
    protected void addPaginationParameterExtractors(
        Map<String, Function<UserRoleNameModel, Object>> map
    ) {
        map.put(
            UserRoleNamePaginationSortOrder.ID_ASC.toString(),
            model -> new IdAscUserRoleNamePaginationParameter(
                model.getId()
            )
        );
        map.put(
            UserRoleNamePaginationSortOrder.ID_DESC.toString(),
            model -> new IdDescUserRoleNamePaginationParameter(
                model.getId()
            )
        );
    }
}
