/*
 * Copyright 2024 youngmonkeys.org
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

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.util.Numbers.toIntOrZero;

public final class Versions {

    private Versions() {}

    public static int compareVersions(
        String version1,
        String version2
    ) {
        boolean isVersion1Blank = isBlank(version1);
        boolean isVersion2Blank = isBlank(version2);
        if (isVersion1Blank && isVersion2Blank) {
            return 0;
        }
        if (!isVersion1Blank && isVersion2Blank) {
            return 1;
        }
        if (isVersion1Blank) {
            return -1;
        }
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        int minLength = Math.min(v1Parts.length, v2Parts.length);
        for (int i = 0; i < minLength; ++i) {
            int v1Part = toIntOrZero(v1Parts[i]);
            int v2Part = toIntOrZero(v2Parts[i]);
            if (v1Part > v2Part) {
                return 1;
            } else if (v1Part < v2Part) {
                return -1;
            }
        }
        return 0;
    }
}
