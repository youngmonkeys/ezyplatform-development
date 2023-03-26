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

package org.youngmonkeys.ezyplatform.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionObjects {

    private ReflectionObjects() {}

    public static boolean isEmptyObject(Object obj) {
        return getObjectProperties(
            obj,
            true
        ).isEmpty();
    }

    public static Map<String, Object> getObjectProperties(
        Object obj
    ) {
        return getObjectProperties(obj, false);
    }

    public static Map<String, Object> getObjectProperties(
        Object obj,
        boolean onlyTakeFirstValue
    ) {
        Map<String, Object> properties = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
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
                    value = field.get(obj);
                } else {
                    field.setAccessible(true);
                    value = field.get(obj);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            if (value != null) {
                properties.put(field.getName(), value);
                if (onlyTakeFirstValue) {
                    break;
                }
            }
        }
        return properties;
    }
}
