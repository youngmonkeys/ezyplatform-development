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

import lombok.Getter;

public enum ScheduleType {

    NEVER(0L),
    MILLISECONDS(1L),
    SECONDS(1000L),
    MINUTES(60 * 1000L),
    HOURS(60 * 60 * 1000L),
    DAYS(24 * 60 * 60 * 1000L),
    WEEKS(7 * 24 * 60 * 60 * 1000L),
    MONTHS(30 * 24 * 60 * 60 * 1000L),
    YEARS(365 * 24 * 60 * 60 * 1000L);

    @Getter
    private final long millisSeconds;

    ScheduleType(long millisSeconds) {
        this.millisSeconds = millisSeconds;
    }
}
