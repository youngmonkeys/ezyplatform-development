/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class CommonEntityModel {
    private long id;
    private String name;
    private String displayName;
    private String url;
    private Map<String, Object> properties;

    public static CommonEntityModel defaultEntity(
        long id,
        String name
    ) {
        return CommonEntityModel.builder()
            .id(id)
            .name(name)
            .build();
    }

    public Map<String, Object> getProperties() {
        return properties != null
            ? properties
            : Collections.emptyMap();
    }
}
