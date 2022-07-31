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

public enum DurationType {

    NANOSECOND("ns", 0),
    MILLISECOND("ms", 1),
    SECOND("s", 1000L),
    MINUTE("m", 60 * 1000L),
    HOUR("h", 60 * 60 * 1000L),
    DAY("d", 24 * 60 * 60 * 1000L),
    WEEK("w", 7 * 24 * 60 * 60 * 1000L),
    MONTH("M", 30 * 24 * 60 * 60 * 1000L),
    YEAR("y", 365 * 24 * 60 * 60 * 1000L);

    @Getter
    private final String symbol;

    @Getter
    private final long millis;

    private static final Map<String, DurationType> MAP =
        EzyEnums.enumMap(DurationType.class, it -> it.symbol);

    DurationType(String symbol, long millis) {
        this.symbol = symbol;
        this.millis = millis;
    }

    public static DurationType of(String symbol) {
        return MAP.get(symbol);
    }
}
