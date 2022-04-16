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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
public class DefaultPaginationParameterConverter
    implements PaginationParameterConverter {

    private final ObjectMapper objectMapper;

    @Override
    public String serialize(Object paginationParameter) {
        if (paginationParameter == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(paginationParameter);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid pagination parameter", e);
        }
    }

    @Override
    public <T> T deserialize(
        String pageToken,
        Class<T> paginationParameterType
    ) {
        if (pageToken == null) {
            return null;
        }
        try {
            return objectMapper.readValue(pageToken, paginationParameterType);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid pagination parameter value", e);
        }
    }

    @Override
    public <T> T deserialize(
        String pageToken,
        TypeReference<T> paginationParameterTypeReference
    ) {
        if (pageToken == null) {
            return null;
        }
        try {
            return objectMapper.readValue(pageToken, paginationParameterTypeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid pagination parameter value", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> serializeToMap(Object paginationParameter) {
        if (paginationParameter == null) {
            return null;
        }
        return (Map<String, Object>) objectMapper.convertValue(
            paginationParameter,
            Map.class
        );
    }

    @Override
    public <T> T deserializeFromMap(
        Map<String, Object> value,
        Class<T> paginationParameterType
    ) {
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(
            value,
            paginationParameterType
        );
    }
}
