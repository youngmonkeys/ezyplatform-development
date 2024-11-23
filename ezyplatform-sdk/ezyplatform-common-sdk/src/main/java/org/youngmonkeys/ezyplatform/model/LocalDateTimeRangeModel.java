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

package org.youngmonkeys.ezyplatform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youngmonkeys.ezyplatform.constant.ExtendedMonth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateTimeRangeModel {
    private LocalDateTime start;
    private LocalDateTime end;

    public static final LocalDateTimeRangeModel DEFAULT =
        new LocalDateTimeRangeModel();

    public static LocalDateTimeRangeModel parseYears(
        String value
    ) {
        if (isBlank(value)) {
            return LocalDateTimeRangeModel.DEFAULT;
        }
        LocalDate start = null;
        LocalDate end = null;
        if (value.contains("|")) {
            String[] strs = value.split("\\|");
            try {
                int year = Integer.parseInt(strs[0]);
                start = LocalDate.of(year, 1, 1);
            } catch (Exception e) {
                // do nothing
            }
            try {
                int year = Integer.parseInt(strs[1]);
                end = LocalDate.of(year, 1, 1);
            } catch (Exception e) {
                // do nothing
            }
        } else {
            try {
                int year = Integer.parseInt(value);
                start = LocalDate.of(year, 1, 1);
            } catch (Exception e) {
                // do nothing
            }
        }
        return new LocalDateTimeRangeModel(
            start != null ? start.atStartOfDay() : null,
            end != null ? end.atStartOfDay() : null
        );
    }

    public static LocalDateTimeRangeModel parseMonths(
        String value
    ) {
        if (isBlank(value)) {
            return LocalDateTimeRangeModel.DEFAULT;
        }
        LocalDate start = null;
        LocalDate end = null;
        if (value.contains("|")) {
            String[] strs = value.split("\\|");
            try {
                start = LocalDate.parse(strs[0] + "-01");
            } catch (Exception e) {
                // do nothing
            }
            try {
                end = LocalDate.parse(strs[1] + "-01");
            } catch (Exception e) {
                // do nothing
            }
        } else {
            try {
                start = LocalDate.parse(value + "-01");
            } catch (Exception e) {
                // do nothing
            }
        }
        return new LocalDateTimeRangeModel(
            start != null ? start.atStartOfDay() : null,
            end != null ? end.atStartOfDay() : null
        );
    }

    public static LocalDateTimeRangeModel parseDays(
        String value
    ) {
        if (isBlank(value)) {
            return LocalDateTimeRangeModel.DEFAULT;
        }
        LocalDate start = null;
        LocalDate end = null;
        if (value.contains("|")) {
            String[] strs = value.split("\\|");
            try {
                start = LocalDate.parse(strs[0]);
            } catch (Exception e) {
                // do nothing
            }
            try {
                end = LocalDate.parse(strs[1]);
            } catch (Exception e) {
                // do nothing
            }
        } else {
            try {
                start = LocalDate.parse(value);
            } catch (Exception e) {
                // do nothing
            }
        }
        return new LocalDateTimeRangeModel(
            start != null ? start.atStartOfDay() : null,
            end != null ? end.atStartOfDay() : null
        );
    }

    public static LocalDateTimeRangeModel parseDateTimes(
        String value
    ) {
        if (isBlank(value)) {
            return LocalDateTimeRangeModel.DEFAULT;
        }
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (value.contains("|")) {
            String[] strs = value.split("\\|");
            try {
                start = LocalDateTime.parse(strs[0]);
            } catch (Exception e) {
                // do nothing
            }
            try {
                end = LocalDateTime.parse(strs[1]);
            } catch (Exception e) {
                // do nothing
            }
        } else {
            try {
                start = LocalDateTime.parse(value);
            } catch (Exception e) {
                // do nothing
            }
        }
        return new LocalDateTimeRangeModel(start, end);
    }

    public static LocalDateTimeRangeModel parseExtendedMonth(
        String value
    ) {
        return parseExtendedMonth(LocalDateTime.now(), value);
    }

    public static LocalDateTimeRangeModel parseExtendedMonth(
        LocalDateTime now,
        String value
    ) {
        if (isBlank(value)) {
            return LocalDateTimeRangeModel.DEFAULT;
        }
        LocalDate start = null;
        LocalDate end = null;
        ExtendedMonth extendedMonth = ExtendedMonth.of(value);
        if (extendedMonth == ExtendedMonth.THIS_MONTH) {
            start = LocalDate.of(now.getYear(), now.getMonth(), 1);
        } else if (extendedMonth == ExtendedMonth.LAST_MONTH) {
            end = LocalDate.of(now.getYear(), now.getMonth(), 1);
            start = end.minusMonths(1);
        } else if (extendedMonth != null) {
            Month month = extendedMonth.toMonth();
            start = LocalDate.of(now.getYear(), month, 1);
            end = start.plusMonths(1);
        }
        return new LocalDateTimeRangeModel(
            start != null ? start.atStartOfDay() : null,
            end != null ? end.atStartOfDay() : null
        );
    }
}