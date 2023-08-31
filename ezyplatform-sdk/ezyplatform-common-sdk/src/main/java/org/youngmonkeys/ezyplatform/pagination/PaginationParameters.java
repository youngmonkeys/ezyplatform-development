/*
 * Copyright 2023 youngmonkeys.org
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

public final class PaginationParameters {

    private PaginationParameters() {}

    public static String makeOrderByAsc(
        boolean nextPage,
        String... fieldNames
    ) {
        return makeOrderBy(
            SortOrder.ASC,
            nextPage,
            fieldNames
        );
    }

    public static String makeOrderByDesc(
        boolean nextPage,
        String... fieldNames
    ) {
        return makeOrderBy(
            SortOrder.DESC,
            nextPage,
            fieldNames
        );
    }

    public static String makeOrderBy(
        SortOrder sortOrder,
        boolean nextPage,
        String... fieldNames
    ) {
        return makeOrderBy(
            "e",
            sortOrder,
            nextPage,
            fieldNames
        );
    }

    public static String makeOrderBy(
        String entityName,
        SortOrder sortOrder,
        boolean nextPage,
        String... fieldNames
    ) {
        SortOrder actualSortOrder = nextPage
            ? sortOrder
            : sortOrder.getReverse();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fieldNames.length; ++i) {
            builder
                .append(entityName)
                .append('.')
                .append(fieldNames[i]);
            builder
                .append(' ')
                .append(actualSortOrder);
            if (i < fieldNames.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static String makePaginationConditionAsc(
        boolean nextPage,
        String... fieldNames
    ) {
        return makePaginationCondition(
            SortOrder.ASC,
            nextPage,
            fieldNames
        );
    }

    public static String makePaginationConditionDesc(
        boolean nextPage,
        String... fieldNames
    ) {
        return makePaginationCondition(
            SortOrder.DESC,
            nextPage,
            fieldNames
        );
    }

    public static String makePaginationCondition(
        SortOrder sortOrder,
        boolean nextPage,
        String... fieldNames
    ) {
        return makePaginationCondition(
            "e",
            sortOrder,
            nextPage,
            fieldNames
        );
    }

    public static String makePaginationCondition(
        String entityName,
        SortOrder sortOrder,
        boolean nextPage,
        String... fieldNames
    ) {
        SortOrder actualSortOrder = nextPage
            ? sortOrder
            : sortOrder.getReverse();
        int fieldCount = fieldNames.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fieldCount; ++i) {
            builder
                .append(entityName)
                .append('.')
                .append(fieldNames[i])
                .append(' ')
                .append(actualSortOrder.getSign())
                .append(" :")
                .append(fieldNames[i]);
            if (fieldCount > 1 && i < fieldCount - 1) {
                builder.append(" OR (");
                for (int k = 0; k <= i; ++k) {
                    builder
                        .append(entityName)
                        .append('.')
                        .append(fieldNames[k])
                        .append(" = :")
                        .append(fieldNames[k])
                        .append(" AND ");
                }
            }
        }
        for (int i = 1; i < fieldNames.length; ++i) {
            builder.append(')');
        }
        return builder.toString();
    }
}
