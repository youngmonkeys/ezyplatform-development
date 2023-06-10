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

import com.tvd12.ezyfox.util.EzyMapBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class Durations {

    private static final Map<String, Long> MILLIS_BY_SYMBOL =
        EzyMapBuilder.mapBuilder()
            .put("ms", 1L)
            .put("s", 1000L)
            .put("m", 60 * 1000L)
            .put("h", 60 * 60 * 1000L)
            .put("d", 24 * 60 * 60 * 1000L)
            .put("w", 7 * 24 * 60 * 60 * 1000L)
            .put("M", 30 * 24 * 60 * 60 * 1000L)
            .put("y", 365 * 24 * 60 * 60 * 1000L)
            .toMap();

    private Durations() {}

    public static long durationStringToMillis(String str) {
        if (isBlank(str)) {
            return 0;
        }
        long answer = 0;
        long strLength = str.length();
        StringBuilder number = new StringBuilder();
        StringBuilder lastSymbol = new StringBuilder();
        for (int i = 0; i < strLength; ++i) {
            char ch = str.charAt(i);
            if (ch >= '0' && ch <= '9') {
                number.append(ch);
                lastSymbol.delete(0, lastSymbol.length());
            } else {
                lastSymbol.append(ch);
                if ((i + 1) < strLength) {
                    char chNext = str.charAt(i + 1);
                    if (chNext < '0' || chNext > '9') {
                        lastSymbol.append(chNext);
                        ++i;
                    }
                }
                Long millis = MILLIS_BY_SYMBOL.get(lastSymbol.toString());
                int numberLength = number.length();
                if (millis != null && numberLength > 0) {
                    answer += Long.parseLong(number.toString()) * millis;
                    number.delete(0, numberLength);
                } else {
                    answer = -1;
                    break;
                }
            }
        }
        if (lastSymbol.length() == 0) {
            answer = -1;
        }
        return answer;
    }

    public static int calculateDurationInMinute(
        long from,
        long to
    ) {
        if (to <= from) {
            return 0;
        }
        long durationSeconds = (to - from) / 1000;
        return (int) (durationSeconds % 60 == 0
            ? durationSeconds / 60
            : (durationSeconds / 60) + 1);
    }

    public static long calculateDurationToNextTime(
        LocalDateTime start,
        int nextHour,
        int nextMinute,
        int nextSecond
    ) {
        LocalDateTime next = LocalDateTime.of(
            start.getYear(),
            start.getMonth(),
            start.getDayOfMonth(),
            nextHour,
            nextMinute,
            nextSecond
        );
        if (next.isBefore(start)) {
            next = next.plusDays(1L);
        }
        return Duration.between(start, next).toMillis();
    }
}
