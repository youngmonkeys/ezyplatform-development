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

package org.youngmonkeys.ezyplatform.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

public class EditableMetadataManager {

    protected final Map<String, String> metadataTypeByName
        = new ConcurrentHashMap<>();
    protected final Map<String, Object> defaultValueByName
        = new ConcurrentHashMap<>();

    public void addMetadataNameAndType(
        String name,
        String type
    ) {
        this.metadataTypeByName.put(name, type);
    }

    public void addMetadataNameAndTypeMap(
        Map<String, String> typeByName
    ) {
        this.metadataTypeByName.putAll(typeByName);
    }

    public void setMetadataDefaultValue(
        String name,
        Object defaultValue
    ) {
        this.defaultValueByName.put(name, defaultValue);
    }

    public void setMetadataDefaultValues(
        Map<String, String> defaultValues
    ) {
        this.defaultValueByName.putAll(defaultValues);
    }

    public String getMetadataTypeByName(String name) {
        return metadataTypeByName.get(name);
    }

    public Object getMetadataDefaultValueByName(String name) {
        return defaultValueByName.get(name);
    }

    public Set<String> getMetadataNames() {
        return new HashSet<>(metadataTypeByName.keySet());
    }

    public List<String> getMetadataTypeMessageKeys() {
        return newArrayList(
            metadataTypeByName.values(),
            String::toLowerCase
        );
    }

    public List<String> getSortedMetadataNames() {
        return metadataTypeByName
            .keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public Map<String, String> getMetadataNameTypeMap() {
        return new HashMap<>(metadataTypeByName);
    }

    public Map<String, Object> getDefaultMetadataValueMap() {
        return new HashMap<>(defaultValueByName);
    }
}
