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

import java.util.Locale;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public enum EnvironmentType {

    LOCAL,
    ALPHA,
    BETA,
    STAGING,
    PRODUCTION;

    private static final Map<String, EnvironmentType> MAP_BY_NAME =
        EzyEnums.enumMap(EnvironmentType.class, EnvironmentType::toString);

    public static EnvironmentType of(
        String name
    ) {
        if (isBlank(name)) {
            return null;
        }
        EnvironmentType answer = MAP_BY_NAME.get(name);
        if (answer == null) {
            answer = MAP_BY_NAME.get(name.toUpperCase(Locale.ROOT));
        }
        return answer;
    }

    public static EnvironmentType of(
        String name,
        EnvironmentType defaultValue
    ) {
        EnvironmentType answer = of(name);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public boolean equalsValue(String value) {
        return toString().equals(value);
    }
}
