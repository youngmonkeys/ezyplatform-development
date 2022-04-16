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

package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezyfox.reflect.EzyClasses;
import com.tvd12.ezyfox.sercurity.EzyBase64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;

public abstract class ComplexPaginationParameterConverter<S, M> {

    private final PaginationParameterConverter converter;
    private final Map<S, String> defaultPageTokenBySortOrder;
    private final Map<Object, Class<?>> paginationParameterTypeBySortOrder;
    private final Map<S, Function<M, Object>> paginationParameterExtractorBySortOrder;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ComplexPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        this.converter = converter;
        this.defaultPageTokenBySortOrder = new HashMap<>();
        this.paginationParameterTypeBySortOrder = new HashMap<>();
        mapPaginationParametersToTypes(
            (Map) paginationParameterTypeBySortOrder
        );
        List<S> sortOrders = new ArrayList<>(
            (Set) paginationParameterTypeBySortOrder.keySet()
        );
        for (S sortOrder : sortOrders) {
            Class<?> paginationParameterType =
                paginationParameterTypeBySortOrder.get(sortOrder);
            paginationParameterTypeBySortOrder.put(
                sortOrder.toString(),
                paginationParameterType
            );
            defaultPageTokenBySortOrder.put(
                sortOrder,
                EzyBase64.encodeUtf(
                    converter.serialize(
                        new PaginationParameterWrapper<>(
                            sortOrder,
                            converter.serializeToMap(
                                EzyClasses.newInstance(paginationParameterType)
                            )
                        )
                    )
                )
            );
        }
        this.paginationParameterExtractorBySortOrder = new HashMap<>();
        addPaginationParameterExtractors(paginationParameterExtractorBySortOrder);
    }

    public String serialize(
        S sortOrder,
        M model
    ) {
        Function<M, Object> extractor = paginationParameterExtractorBySortOrder
            .get(sortOrder);
        if (extractor == null) {
            throw new IllegalArgumentException(
                "there is no extractor map to sort order: " + sortOrder
            );
        }
        return converter.serialize(
            new PaginationParameterWrapper<>(
                sortOrder,
                converter.serializeToMap(
                    extractor.apply(model)
                )
            )
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T deserialize(String pageToken) {
        PaginationParameterWrapper<S> wrapper = converter.deserialize(
            pageToken,
            PaginationParameterWrapper.class
        );
        Class paginationParameterType = paginationParameterTypeBySortOrder
            .get(wrapper.sortOrder);
        if (paginationParameterType == null) {
            throw new IllegalArgumentException(
                "there is no pagination parameter type map to sort order: " + wrapper.sortOrder
            );
        }
        return (T) converter.deserializeFromMap(
            wrapper.value,
            paginationParameterType
        );
    }

    public String getDefaultPageToken(
        S sortOrder
    ) {
        return defaultPageTokenBySortOrder.get(sortOrder);
    }

    protected abstract void mapPaginationParametersToTypes(
        Map<S, Class<?>> map
    );

    protected abstract void addPaginationParameterExtractors(
        Map<S, Function<M, Object>> map
    );

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaginationParameterWrapper<S> {
        private S sortOrder;
        private Map<String, Object> value;
    }
}
