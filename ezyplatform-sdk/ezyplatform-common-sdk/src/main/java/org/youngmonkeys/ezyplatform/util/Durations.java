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

package org.youngmonkeys.ezyplatform.util;

import org.youngmonkeys.ezyplatform.constant.DurationType;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.PATTERN_DURATION_STRING;

public final class Durations {

    private Durations() {}

    public static boolean isDurationString(String str) {
        return str != null && str.matches(PATTERN_DURATION_STRING);
    }

    public static boolean isNotDurationString(String str) {
        return !isDurationString(str);
    }

    public static long durationStringToMillis(String str) {
        return durationStringToMillis(str, true);
    }

    public static long durationStringToMillis(
        String str,
        boolean needCheckInputStr
    ) {
        if (needCheckInputStr && isNotDurationString(str)) {
            return -1;
        }
        int strLength = str.length();
        char mostLastCh = str.charAt(strLength - 2);
        int lastValueIndexOffset =
            mostLastCh >= '0' && mostLastCh <= '9' ? 1 : 2;
        String value = str.substring(0, strLength - lastValueIndexOffset);
        String symbol = str.substring(strLength - lastValueIndexOffset);
        DurationType durationType = DurationType.of(symbol);
        return Long.parseLong(value) * durationType.getMillis();
    }
}
