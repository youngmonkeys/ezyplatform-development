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

package org.youngmonkeys.ezyplatform.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public interface ParameterMapFetcher {

    default Map<String, Object> getParameters() {
        Map<String, Object> answer = new HashMap<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())
            ) {
                continue;
            }
            Object value;
            try {
                if (Modifier.isPublic(field.getModifiers())) {
                    value = field.get(this);
                } else {
                    field.setAccessible(true);
                    value = field.get(this);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            if (value != null) {
                answer.put(field.getName(), value);
            }
        }
        return answer;
    }
}
