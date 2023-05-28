/*
 * Copyright 2022 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.database.query.EzyQueryData;
import com.tvd12.ezydata.database.query.EzyQueryMethodType;
import com.tvd12.ezydata.jpa.repository.EzyJpaRepository;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyGenerics;
import org.youngmonkeys.ezyplatform.data.PaginationParameter;
import org.youngmonkeys.ezyplatform.data.StorageFilter;
import org.youngmonkeys.ezyplatform.pagination.CommonPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;
import org.youngmonkeys.ezyplatform.pagination.OffsetPaginationParameter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tvd12.ezyfox.io.EzyCollections.isEmpty;
import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

/**
 * For pagination business.
 *
 * @param <F> the filter value type
 * @param <P> the pagination value type (inclusive or exclusive value type)
 * @param <I> the id type
 * @param <E> the entity type
 * @param <R> the query result type
 */
@SuppressWarnings("MethodCount")
public class PaginationResultRepository<F, P, I, E, R> extends EzyJpaRepository<I, E> {

    protected final Class<R> resultType = getResultType();

    public List<R> findFirstElements(
        F filter,
        int limit
    ) {
        return findNextElements(
            filter,
            null,
            limit
        );
    }

    public List<R> findNextElements(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return findElements(
            filter,
            paginationParameter,
            limit,
            true
        );
    }

    public List<R> findLastElements(
        F filter,
        int limit
    ) {
        return findPreviousElements(
            filter,
            null,
            limit
        );
    }

    public List<R> findPreviousElements(
        F filter,
        P paginationParameter,
        int limit
    ) {
        return findElements(
            filter,
            paginationParameter,
            limit,
            false
        );
    }

    public long countElements(F filter) {
        EzyQueryData queryData = makeQuery(
            EzyQueryMethodType.COUNT,
            filter,
            null,
            true
        );
        return countByQueryString(
            queryData.getQuery(),
            queryData.getParameterMap()
        );
    }

    @SuppressWarnings("unchecked")
    private List<R> findElements(
        F filter,
        P paginationParameter,
        int limit,
        boolean nextPage
    ) {
        EzyQueryData query = makeQuery(
            EzyQueryMethodType.FIND,
            filter,
            paginationParameter,
            nextPage
        );
        int actualOffset = 0;
        int actualLimit = limit;
        if (paginationParameter instanceof OffsetPaginationParameter) {
            int offset = ((OffsetPaginationParameter) paginationParameter)
                .getOffset();
            actualLimit = (offset >= 0) ? limit : limit + offset;
            actualOffset = Math.max(offset, 0);
        }
        return resultType != entityType
            ? fetchListByQueryString(
                query.getQuery(),
                query.getParameterMap(),
                resultType,
                actualOffset,
                actualLimit
            )
            : (List<R>) findListByQueryString(
                query.getQuery(),
                query.getParameterMap(),
                actualOffset,
                actualLimit
            );
    }

    @SuppressWarnings("MethodLength")
    private EzyQueryData makeQuery(
        EzyQueryMethodType methodType,
        F filter,
        P paginationParameter,
        boolean nextPage
    ) {
        StringBuilder queryString = new StringBuilder()
            .append("SELECT");
        boolean distinct = isDistinct();
        if (methodType == EzyQueryMethodType.COUNT) {
            queryString.append(" COUNT(");
            if (distinct) {
                queryString.append(" DISTINCT ");
            }
            queryString
                .append(getCountField(filter))
                .append(")");
        } else {
            if (distinct) {
                queryString.append(" DISTINCT");
            }
            List<String> selectionFields = getSelectionFields(
                filter,
                paginationParameter
            );
            if (isEmpty(selectionFields)) {
                queryString.append(" e");
            } else {
                queryString
                    .append(" ")
                    .append(String.join(",", selectionFields));
            }
        }
        queryString.append(" FROM ")
            .append(getFromName())
            .append(" e");
        decorateQueryStringBeforeWhere(
            queryString,
            filter,
            paginationParameter
        );
        String matchingCondition = makeMatchingCondition(filter);
        String paginationCondition =
            (methodType == EzyQueryMethodType.COUNT || paginationParameter == null)
                ? EMPTY_STRING
                : makePaginationCondition(paginationParameter, nextPage);
        boolean matchingConditionNotBlank = isNotBlank(matchingCondition);
        boolean paginationConditionNotBlank = isNotBlank(paginationCondition);
        if (matchingConditionNotBlank || paginationConditionNotBlank) {
            queryString.append(" WHERE ");
            if (matchingConditionNotBlank) {
                queryString.append(matchingCondition);
            }
            if (matchingConditionNotBlank && paginationConditionNotBlank) {
                queryString.append(" AND ");
            }
            if (paginationConditionNotBlank) {
                queryString
                    .append("(")
                    .append(paginationCondition)
                    .append(")");
            }
        }
        String groupBy = makeGroupBy(filter, paginationParameter);
        if (isNotBlank(groupBy)) {
            queryString.append(" GROUP BY ").append(groupBy);
        }
        if (methodType == EzyQueryMethodType.FIND) {
            String orderBy = makeOrderBy(filter, paginationParameter, nextPage);
            if (isNotBlank(orderBy)) {
                queryString.append(" ORDER BY ").append(orderBy);
            }
        }
        return EzyQueryData.builder()
            .parameters(getQueryParameters(methodType, filter, paginationParameter))
            .query(queryString.toString())
            .build();
    }

    protected String getFromName() {
        return entityType.getSimpleName();
    }

    protected String getCountField(F filter) {
        if (filter instanceof CommonStorageFilter) {
            return ((CommonStorageFilter) filter).countField();
        }
        return getCountField();
    }

    protected String getCountField() {
        return "e";
    }

    protected List<String> getSelectionFields(
        F filter,
        P paginationParameter
    ) {
        return Stream.of(
            getSelectionFields(filter),
            getPaginationSelectionFields(paginationParameter),
            getSelectionFields()
        )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    protected List<String> getSelectionFields(F filter) {
        if (filter instanceof CommonStorageFilter) {
            return ((CommonStorageFilter) filter)
                .selectionFields();
        }
        return Collections.emptyList();
    }

    protected List<String> getSelectionFields() {
        return Collections.emptyList();
    }

    protected List<String> getPaginationSelectionFields(P paginationParameter) {
        if (paginationParameter instanceof CommonPaginationParameter) {
            return ((CommonPaginationParameter) paginationParameter)
                .selectionFields();
        }
        return Collections.emptyList();
    }

    protected boolean isDistinct() {
        return false;
    }

    protected void decorateQueryStringBeforeWhere(
        StringBuilder queryString,
        F filter,
        P paginationParameter
    ) {
        decorateQueryStringBeforeWhere(
            queryString,
            filter
        );
        decoratePaginationQueryStringBeforeWhere(
            queryString,
            paginationParameter
        );
        decorateQueryStringBeforeWhere(queryString);
    }

    protected void decorateQueryStringBeforeWhere(
        StringBuilder queryString,
        F filter
    ) {
        if (filter instanceof CommonStorageFilter) {
            ((CommonStorageFilter) filter)
                .decorateQueryStringBeforeWhere(queryString);
        }
    }

    protected void decorateQueryStringBeforeWhere(
        StringBuilder queryString
    ) {}

    protected void decoratePaginationQueryStringBeforeWhere(
        StringBuilder queryString,
        P paginationParameter
    ) {
        if (paginationParameter instanceof CommonPaginationParameter) {
            ((CommonPaginationParameter) paginationParameter)
                .decorateQueryStringBeforeWhere(queryString);
        }
    }

    protected String makeMatchingCondition(F filter) {
        if (filter instanceof CommonStorageFilter) {
            return ((CommonStorageFilter) filter)
                .matchingCondition();
        }
        return makeMatchingCondition();
    }

    protected String makeMatchingCondition() {
        return EMPTY_STRING;
    }

    protected String makePaginationCondition(
        P paginationParameter,
        boolean nextPage
    ) {
        if (paginationParameter instanceof CommonPaginationParameter) {
            return ((CommonPaginationParameter) paginationParameter)
                .paginationCondition(nextPage);
        }
        return makePaginationCondition(nextPage);
    }

    protected String makePaginationCondition(boolean nextPage) {
        return EMPTY_STRING;
    }

    protected String makeGroupBy(F filter, P paginationParameter) {
        return Stream.of(
            makeGroupBy(filter),
            makePaginationGroupBy(paginationParameter),
            makeGroupBy()
        )
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.joining());
    }

    protected String makeGroupBy(F filter) {
        if (filter instanceof CommonStorageFilter) {
            return ((CommonStorageFilter) filter).groupBy();
        }
        return EMPTY_STRING;
    }

    protected String makeGroupBy() {
        return EMPTY_STRING;
    }

    protected String makePaginationGroupBy(P paginationParameter) {
        if (paginationParameter instanceof CommonPaginationParameter) {
            return ((CommonPaginationParameter) paginationParameter)
                .groupBy();
        }
        return EMPTY_STRING;
    }

    protected String makeOrderBy(
        F filter,
        P paginationParameter,
        boolean nextPage
    ) {
        return makeOrderBy(paginationParameter, nextPage);
    }

    protected String makeOrderBy(P paginationParameter, boolean nextPage) {
        if (paginationParameter instanceof CommonPaginationParameter) {
            return ((CommonPaginationParameter) paginationParameter)
                .orderBy(nextPage);
        } else if (paginationParameter instanceof OffsetPaginationParameter) {
            return ((OffsetPaginationParameter) paginationParameter)
                .getOrderBy();
        }
        return makeOrderBy(nextPage);
    }

    protected String makeOrderBy(boolean nextPage) {
        return EMPTY_STRING;
    }

    private Map<String, Object> getQueryParameters(
        EzyQueryMethodType methodType,
        F filter,
        P paginationParameter
    ) {
        Map<String, Object> parameters = new HashMap<>(
            getFilterQueryParameters(filter)
        );
        if (methodType != EzyQueryMethodType.COUNT) {
            parameters.putAll(getPaginationQueryParameters(paginationParameter));
        }
        return parameters;
    }

    protected Map<String, Object> getFilterQueryParameters(F filter) {
        if (filter instanceof StorageFilter) {
            return ((StorageFilter) filter).getParameters();
        }
        return filter == null
            ? Collections.emptyMap()
            : Collections.singletonMap("filter", filter);
    }

    protected Map<String, Object> getPaginationQueryParameters(
        P paginationParameter
    ) {
        if (paginationParameter instanceof PaginationParameter) {
            return ((PaginationParameter) paginationParameter).getParameters();
        }
        return paginationParameter == null
            ? Collections.emptyMap()
            : Collections.singletonMap("parameter", paginationParameter);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<E> getEntityType() {
        return EzyGenerics.getGenericClassArguments(
            getClass().getGenericSuperclass(),
            5
        )[3];
    }

    @SuppressWarnings("unchecked")
    protected Class<R> getResultType() {
        return EzyGenerics.getGenericClassArguments(
            getClass().getGenericSuperclass(),
            5
        )[4];
    }
}
