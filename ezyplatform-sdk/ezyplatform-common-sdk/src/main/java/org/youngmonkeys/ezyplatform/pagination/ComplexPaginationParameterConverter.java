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
import com.tvd12.ezyfox.security.EzyBase64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ComplexPaginationParameterConverter<S, M> {

    private final PaginationParameterConverter converter;
    private final Map<S, String> defaultPageTokenBySortOrder;
    private final Map<Object, Class<?>> paginationParameterTypeBySortOrder;
    private final Map<S, Function<M, Object>> paginationParameterExtractorBySortOrder;

    @SuppressWarnings("unchecked")
    public ComplexPaginationParameterConverter(
        PaginationParameterConverter converter
    ) {
        this.converter = converter;
        this.defaultPageTokenBySortOrder = new HashMap<>();
        this.paginationParameterTypeBySortOrder = new HashMap<>();
        mapPaginationParametersToTypes(
            (Map<S, Class<?>>) paginationParameterTypeBySortOrder
        );
        new HashMap<>((Map<S, Class<?>>) paginationParameterTypeBySortOrder)
            .forEach(this::registerPaginationParameter);
        this.paginationParameterExtractorBySortOrder = new HashMap<>();
        addPaginationParameterExtractors(paginationParameterExtractorBySortOrder);
    }

    private void registerPaginationParameter(
        S sortOrder,
        Class<?> paginationParameterType
    ) {
        paginationParameterTypeBySortOrder.put(
            sortOrder,
            paginationParameterType
        );
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

    public void registerPaginationParameter(
        S sortOrder,
        Class<?> paginationParameterType,
        Function<M, Object> paginationParameterExtractor
    ) {
        registerPaginationParameter(sortOrder, paginationParameterType);
        paginationParameterExtractorBySortOrder.put(
            sortOrder,
            paginationParameterExtractor
        );
    }

    @SuppressWarnings("unchecked")
    public String serialize(
        S sortOrder,
        M model
    ) {
        Function<M, Object> extractor = paginationParameterExtractorBySortOrder
            .get(sortOrder);
        if (extractor == null) {
            extractor = (Function<M, Object>) Function.identity();
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
                "there is no pagination parameter type map to sort order: " +
                    wrapper.sortOrder
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

    protected void addPaginationParameterExtractors(
        Map<S, Function<M, Object>> map
    ) {}

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaginationParameterWrapper<S> {
        private S sortOrder;
        private Map<String, Object> value;
    }
}
