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

package org.youngmonkeys.ezyplatform.socket.util;

import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;

import java.util.Map;

public final class EzyUserLoginEvents {

    private EzyUserLoginEvents() {}

    @SuppressWarnings("unchecked")
    public static <T> T getUserPropertyByName(
        EzyUserLoginEvent event,
        Object propertyName
    ) {
        Map<Object, Object> props = event.getUserProperties();
        return props == null
            ? null
            : (T) props.get(propertyName);
    }

    public static <T> T getUserPropertyByType(
        EzyUserLoginEvent event,
        Class<T> propertyType
    ) {
        return getUserPropertyByName(event, propertyType);
    }
}
