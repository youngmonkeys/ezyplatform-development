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

package org.youngmonkeys.ezyplatform.constant;

import com.tvd12.ezyfox.util.EzyEnums;

import java.util.Map;

public enum SocketUserType implements ISocketUserType {
    ADMIN,
    ANONYMOUS,
    USER;

    private static final Map<String, SocketUserType> MAP_BY_NAME =
        EzyEnums.enumMap(
            SocketUserType.class,
            SocketUserType::toString
        );

    public static SocketUserType of(String value) {
        return MAP_BY_NAME.get(value);
    }

    public static SocketUserType of(
        String value,
        SocketUserType defaultType
    ) {
        return MAP_BY_NAME.getOrDefault(
            value,
            defaultType
        );
    }

    public boolean equalsValue(String value) {
        return toString().equals(value);
    }
}
