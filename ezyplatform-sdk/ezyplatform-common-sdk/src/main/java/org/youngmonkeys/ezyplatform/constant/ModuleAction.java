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
import lombok.Getter;

import java.util.Map;

public enum ModuleAction {
    ACTIVATE("activate"),
    DEACTIVATE("deactivate"),
    DELETE("delete");

    @Getter
    private final String name;

    private static final Map<String, ModuleAction> MAP =
        EzyEnums.enumMap(ModuleAction.class, it -> it.name);

    ModuleAction(String name) {
        this.name = name;
    }

    public static ModuleAction of(String name) {
        return name == null ? null : MAP.get(name);
    }
}
