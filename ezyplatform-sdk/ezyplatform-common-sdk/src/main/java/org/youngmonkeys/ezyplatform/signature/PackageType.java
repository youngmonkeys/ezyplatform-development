/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.signature;

import lombok.Getter;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;

@Getter
public enum PackageType {

    PLATFORM("platform", "ezyplatform"),
    SDK("sdk", "ezyplatform-sdk"),
    MODULE("module", NULL_STRING);

    private final String value;
    private final String fixedPackageName;

    PackageType(String value, String fixedPackageName) {
        this.value = value;
        this.fixedPackageName = fixedPackageName;
    }

    public static PackageType of(String value) {
        for (PackageType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
