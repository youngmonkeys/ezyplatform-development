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

package org.youngmonkeys.ezyplatform.util;

import java.util.HashMap;
import java.util.Map;

public final class Maps {

    private Maps() {}

    @SafeVarargs
    public static <K, V> Map<K, V> merge(
        Map<K, V>... maps
    ) {
        Map<K, V> newMap = new HashMap<>();
        for (Map<K, V> map : maps) {
            if (map != null) {
                newMap.putAll(map);
            }
        }
        return newMap;
    }
}
