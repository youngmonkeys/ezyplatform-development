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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.tvd12.ezyfox.io.EzyDates.toInstant;

public final class LocalDateTimes {

    private LocalDateTimes() {}

    public static long toTimestamp(
        LocalDate localDate,
        ZoneId zoneId
    ) {
        return localDate == null
            ? 0L
            : toInstant(localDate, zoneId).toEpochMilli();
    }

    public static long toTimestamp(
        LocalDateTime localDateTime,
        ZoneId zoneId
    ) {
        return localDateTime == null
            ? 0L
            : toInstant(localDateTime, zoneId).toEpochMilli();
    }
}
