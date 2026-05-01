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

package org.youngmonkeys.ezyplatform.test.time;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ClockProxyTest {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Instant NOW = Instant.parse("2024-01-02T03:04:05.006Z");

    private final ClockProxy sut = new ClockProxy(
        Clock.fixed(NOW, ZONE_ID),
        ZONE_ID
    );

    @Test
    public void nowDateTest() {
        // given
        LocalDate expectation = LocalDate.of(2024, 1, 2);

        // when
        LocalDate actual = sut.nowDate();

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void nowDateTimeTest() {
        // given
        LocalDateTime expectation = LocalDateTime.of(
            2024,
            1,
            2,
            10,
            4,
            5,
            6 * 1000 * 1000
        );

        // when
        LocalDateTime actual = sut.nowDateTime();

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toTimestampWithDateTest() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 2);
        long expectation = date
            .atStartOfDay(ZONE_ID)
            .toInstant()
            .toEpochMilli();

        // when
        long actual = sut.toTimestamp(date);

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toTimestampWithNullDateTest() {
        // given
        LocalDate date = null;

        // when
        long actual = sut.toTimestamp(date);

        // then
        Asserts.assertZero(actual);
    }

    @Test
    public void toTimestampWithDateTimeTest() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(
            2024,
            1,
            2,
            10,
            4,
            5,
            6 * 1000 * 1000
        );
        long expectation = dateTime
            .atZone(ZONE_ID)
            .toInstant()
            .toEpochMilli();

        // when
        long actual = sut.toTimestamp(dateTime);

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toTimestampWithNullDateTimeTest() {
        // given
        LocalDateTime dateTime = null;

        // when
        long actual = sut.toTimestamp(dateTime);

        // then
        Asserts.assertZero(actual);
    }

    @Test
    public void toLocalDateTest() {
        // given
        long millis = NOW.toEpochMilli();

        // when
        LocalDate actual = sut.toLocalDate(millis);

        // then
        Asserts.assertEquals(
            actual,
            LocalDate.of(2024, 1, 2)
        );
    }

    @Test
    public void toLocalDateOrNullWithPositiveMillisTest() {
        // given
        long millis = NOW.toEpochMilli();

        // when
        LocalDate actual = sut.toLocalDateOrNull(millis);

        // then
        Asserts.assertEquals(
            actual,
            LocalDate.of(2024, 1, 2)
        );
    }

    @Test
    public void toLocalDateOrNullWithZeroMillisTest() {
        // given
        long millis = 0L;

        // when
        LocalDate actual = sut.toLocalDateOrNull(millis);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void toLocalDateOrNullWithNegativeMillisTest() {
        // given
        long millis = -1L;

        // when
        LocalDate actual = sut.toLocalDateOrNull(millis);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void toLocalDateTimeTest() {
        // given
        long millis = NOW.toEpochMilli();
        LocalDateTime expectation = LocalDateTime.of(
            2024,
            1,
            2,
            10,
            4,
            5,
            6 * 1000 * 1000
        );

        // when
        LocalDateTime actual = sut.toLocalDateTime(millis);

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toLocalDateTimeOrNullWithPositiveMillisTest() {
        // given
        long millis = NOW.toEpochMilli();
        LocalDateTime expectation = LocalDateTime.of(
            2024,
            1,
            2,
            10,
            4,
            5,
            6 * 1000 * 1000
        );

        // when
        LocalDateTime actual = sut.toLocalDateTimeOrNull(millis);

        // then
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toLocalDateTimeOrNullWithZeroMillisTest() {
        // given
        long millis = 0L;

        // when
        LocalDateTime actual = sut.toLocalDateTimeOrNull(millis);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void toLocalDateTimeOrNullWithNegativeMillisTest() {
        // given
        long millis = -1L;

        // when
        LocalDateTime actual = sut.toLocalDateTimeOrNull(millis);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void nowDateTimeIfNullWithValueTest() {
        // given
        LocalDateTime value = LocalDateTime.of(2024, 1, 1, 1, 2, 3);

        // when
        LocalDateTime actual = sut.nowDateTimeIfNull(value);

        // then
        Asserts.assertEquals(actual, value);
    }

    @Test
    public void nowDateTimeIfNullWithNullTest() {
        // given
        LocalDateTime value = null;
        LocalDateTime expectation = LocalDateTime.of(
            2024,
            1,
            2,
            10,
            4,
            5,
            6 * 1000 * 1000
        );

        // when
        LocalDateTime actual = sut.nowDateTimeIfNull(value);

        // then
        Asserts.assertEquals(actual, expectation);
    }
}
