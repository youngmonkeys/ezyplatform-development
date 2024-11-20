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

package org.youngmonkeys.ezyplatform.test.model;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.constant.ExtendedMonth;
import org.youngmonkeys.ezyplatform.model.LocalDateTimeRangeModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public class LocalDateTimeRangeModelTest {

    @Test
    public void parseYearsTest() {
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseYears(null),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseYears("2024"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                null
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseYears("2024|2025"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0)
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseYears("a|b"),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseYears("a"),
            LocalDateTimeRangeModel.DEFAULT
        );
    }

    @Test
    public void parseMonthsTest() {
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseMonths(null),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseMonths("2024-01"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                null
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseMonths("2024-01|2025-02"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2025, 2, 1, 0, 0)
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseMonths("a|b"),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseMonths("a"),
            LocalDateTimeRangeModel.DEFAULT
        );
    }

    @Test
    public void parseDaysTest() {
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDays(null),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDays("2024-01-01"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                null
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDays("2024-01-01|2025-02-03"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2025, 2, 3, 0, 0)
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDays("a|b"),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDays("a"),
            LocalDateTimeRangeModel.DEFAULT
        );
    }

    @Test
    public void parseDateTimesTest() {
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDateTimes(null),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDateTimes("2024-01-01T00:00:00"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                null
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDateTimes("2024-01-01T01:00:00|2025-02-03T04:05:06"),
            new LocalDateTimeRangeModel(
                LocalDateTime.of(2024, 1, 1, 1, 0),
                LocalDateTime.of(2025, 2, 3, 4, 5, 6)
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDateTimes("a|b"),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseDateTimes("a"),
            LocalDateTimeRangeModel.DEFAULT
        );
        Asserts.assertNull(
            LocalDateTimeRangeModel.parseDateTimes("a").getStart()
        );
        Asserts.assertNull(
            LocalDateTimeRangeModel.parseDateTimes("a").getEnd()
        );
    }

    @Test
    public void parseExtendedMonthTest() {
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseExtendedMonth(null),
            LocalDateTimeRangeModel.DEFAULT
        );
        LocalDate now = LocalDate.now();
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseExtendedMonth(ExtendedMonth.THIS_MONTH.toString()),
            new LocalDateTimeRangeModel(
                LocalDate.of(now.getYear(), now.getMonth(), 1).atStartOfDay(),
                null
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseExtendedMonth(ExtendedMonth.LAST_MONTH.toString()),
            new LocalDateTimeRangeModel(
                LocalDate.of(now.getYear(), now.getMonth(), 1).minusMonths(1).atStartOfDay(),
                LocalDate.of(now.getYear(), now.getMonth(), 1).atStartOfDay()
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseExtendedMonth(ExtendedMonth.APRIL.toString()),
            new LocalDateTimeRangeModel(
                LocalDate.of(now.getYear(), Month.APRIL, 1).atStartOfDay(),
                LocalDate.of(now.getYear(), Month.APRIL, 1).plusMonths(1).atStartOfDay()
            )
        );
        Asserts.assertEquals(
            LocalDateTimeRangeModel.parseExtendedMonth("abc"),
            LocalDateTimeRangeModel.DEFAULT
        );
    }
}
