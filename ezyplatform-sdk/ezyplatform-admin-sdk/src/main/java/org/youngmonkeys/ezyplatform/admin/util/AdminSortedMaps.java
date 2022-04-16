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

package org.youngmonkeys.ezyplatform.admin.util;

import java.util.*;

public final class AdminSortedMaps {

    private AdminSortedMaps() {}

    public static SortedMap<String, String> getMapByPrefix(
        Map<String, String> origin,
        String prefix
    ) {
        if (prefix.isEmpty()) {
            return new TreeMap<>(origin);
        }
        int index = 0;
        Map<String, Integer> indexMap = new HashMap<>();
        Map<String, String> valueMap = new HashMap<>();
        for (String key : origin.keySet()) {
            if (key.startsWith(prefix)) {
                String newKey = "";
                if (key.length() > prefix.length() + 1) {
                    newKey = key.substring(prefix.length() + 1);
                }
                indexMap.put(newKey, index++);
                valueMap.put(newKey, origin.get(key));
            }
        }
        SortedMap<String, String> answer = new TreeMap<>(
            Comparator.comparingInt(a -> indexMap.getOrDefault(a, 0))
        );
        answer.putAll(valueMap);
        return answer;
    }
}
