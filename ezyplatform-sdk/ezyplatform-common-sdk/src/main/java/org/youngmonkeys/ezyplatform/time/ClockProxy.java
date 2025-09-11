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

package org.youngmonkeys.ezyplatform.time;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.io.EzyDates;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.util.LocalDateTimes;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@EzySingleton
@AllArgsConstructor
public class ClockProxy {

    private final Clock clock;
    private final ZoneId zoneId;

    public LocalDate nowDate() {
        return nowDateTime().toLocalDate();
    }

    public LocalDateTime nowDateTime() {
        return EzyDates.millisToDateTime(clock.millis(), zoneId);
    }

    public long toTimestamp(LocalDate date) {
        return LocalDateTimes.toTimestamp(date, zoneId);
    }

    public long toTimestamp(LocalDateTime dateTime) {
        return LocalDateTimes.toTimestamp(dateTime, zoneId);
    }

    public LocalDate toLocalDate(long millis) {
        return toLocalDateTime(millis).toLocalDate();
    }

    public LocalDateTime toLocalDateTime(long millis) {
        return EzyDates.millisToDateTime(
            millis,
            zoneId
        );
    }

    public LocalDateTime nowDateTimeIfNull(
        LocalDateTime value
    ) {
        return value != null
            ? value
            : nowDateTime();
    }
}
