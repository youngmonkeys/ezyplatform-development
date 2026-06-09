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

package org.youngmonkeys.ezyplatform.test.data;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.CronExpression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.TreeSet;

public class CronExpressionTest {

    @Test
    public void parseFiveFieldUnixExpressionTest() {
        // given
        String expression = "30 8 1,15 1-3 1-5";

        // when
        CronExpression actual = CronExpression.parse(expression);

        // then
        Asserts.assertEquals(actual.getExpression(), expression);
        Asserts.assertEquals(actual.getSeconds(), set(0));
        Asserts.assertEquals(actual.getMinutes(), set(30));
        Asserts.assertEquals(actual.getHours(), set(8));
        Asserts.assertEquals(actual.getDaysOfMonth(), set(1, 15));
        Asserts.assertEquals(actual.getMonths(), set(1, 2, 3));
        Asserts.assertEquals(actual.getDaysOfWeek(), set(1, 2, 3, 4, 5));
    }

    @Test
    public void parseSixFieldSpringQuartzExpressionTest() {
        // given
        String expression = "15 30 8 * */3 0,6";

        // when
        CronExpression actual = CronExpression.parse(expression);

        // then
        Asserts.assertEquals(actual.getExpression(), expression);
        Asserts.assertEquals(actual.getSeconds(), set(15));
        Asserts.assertEquals(actual.getMinutes(), set(30));
        Asserts.assertEquals(actual.getHours(), set(8));
        Asserts.assertEquals(actual.getDaysOfMonth(), range(1, 31));
        Asserts.assertEquals(actual.getMonths(), set(1, 4, 7, 10));
        Asserts.assertEquals(actual.getDaysOfWeek(), set(0, 6));
    }

    @Test
    public void fiveFieldExpressionUsesZeroSecondTest() {
        // given
        CronExpression expression = CronExpression.parse("* * * * *");
        LocalDateTime time = LocalDateTime.of(
            2026,
            1,
            5,
            8,
            30,
            0
        );

        // when
        LocalDateTime actual = expression.nextTimeOf(time);

        // then
        Asserts.assertEquals(
            actual,
            LocalDateTime.of(2026, 1, 5, 8, 31, 0)
        );
    }

    @Test
    public void sixFieldExpressionUsesSecondFieldTest() {
        // given
        CronExpression expression = CronExpression.parse("15 30 8 * * *");
        LocalDateTime time = LocalDateTime.of(
            2026,
            1,
            5,
            8,
            30,
            14
        );

        // when
        LocalDateTime actual = expression.nextTimeOf(time);

        // then
        Asserts.assertEquals(
            actual,
            LocalDateTime.of(2026, 1, 5, 8, 30, 15)
        );
    }

    @Test
    public void dayOfWeekZeroMeansSundayTest() {
        // given
        CronExpression expression = CronExpression.parse("0 0 0 * * 0");
        LocalDateTime time = LocalDateTime.of(
            2026,
            1,
            3,
            23,
            59,
            59
        );

        // when
        LocalDateTime actual = expression.nextTimeOf(time);

        // then
        Asserts.assertEquals(
            actual,
            LocalDateTime.of(2026, 1, 4, 0, 0, 0)
        );
    }

    @Test
    public void dayOfWeekSevenIsNotSundayTest() {
        // given
        CronExpression expression = CronExpression.parse("0 0 0 * * 7");
        LocalDateTime time = LocalDateTime.of(
            2026,
            1,
            3,
            23,
            59,
            59
        );

        // when
        LocalDateTime actual = expression.nextTimeOf(time);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void rangeWithStepExpressionTest() {
        // given
        CronExpression expression = CronExpression.parse(
            "0 0 9-17/2 * */3 1-5"
        );
        LocalDateTime time = LocalDateTime.of(
            2026,
            1,
            5,
            8,
            59,
            59
        );

        // when
        LocalDateTime actual = expression.nextTimeOf(time);

        // then
        Asserts.assertEquals(expression.getHours(), set(9, 11, 13, 15, 17));
        Asserts.assertEquals(expression.getMonths(), set(1, 4, 7, 10));
        Asserts.assertEquals(
            actual,
            LocalDateTime.of(2026, 1, 5, 9, 0, 0)
        );
    }

    @Test(dataProvider = "toStringCases")
    public void toStringTest(String expression, String expected) {
        Asserts.assertEquals(CronExpression.parse(expression).toString(), expected);
    }

    @DataProvider
    public Object[][] toStringCases() {
        return new Object[][] {
            // high-frequency
            {"* * * * * *",       "every second"},
            {"0/30 * * * * *",    "every 30 seconds"},
            {"0/10 * * * * *",    "every 10 seconds"},
            {"* * * * *",         "every minute"},
            {"*/5 * * * *",       "every 5 minutes"},
            {"*/15 * * * *",      "every 15 minutes"},
            {"*/30 * * * *",      "every 30 minutes"},
            {"0 * * * *",         "every hour"},
            {"0 */2 * * *",       "every 2 hours"},
            // hours in a range with step
            {"0 9-17/2 * * *",    "every 2 hours from 09:00 to 17:00"},
            // specific times — every day
            {"0 8 * * *",         "at 08:00, every day"},
            {"30 8 * * *",        "at 08:30, every day"},
            {"0 0 * * *",         "at 00:00, every day"},
            // multiple hours/minutes
            {"0 8,17 * * *",      "at 08:00 and 17:00, every day"},
            {"0,30 8 * * *",      "at 08:00 and 08:30, every day"},
            {"0 8,12,18 * * *",   "at 08:00, 12:00, and 18:00, every day"},
            // day-of-week constraints
            {"0 8 * * 1",         "at 08:00, every Monday"},
            {"0 8 * * 0",         "at 08:00, every Sunday"},
            {"0 8 * * 1-5",       "at 08:00, every weekday"},
            {"0 8 * * 0,6",       "at 08:00, on weekends"},
            {"0 8 * * 1,3,5",     "at 08:00, every Monday, Wednesday, and Friday"},
            {"0 8 * * 0-2",       "at 08:00, every Sunday through Tuesday"},
            // day-of-month constraints
            {"0 8 1 * *",         "at 08:00, on the 1st of every month"},
            {"0 8 15 * *",        "at 08:00, on the 15th of every month"},
            {"0 8 1,15 * *",      "at 08:00, on the 1st and 15th of every month"},
            {"0 0 1,10,20 * *",   "at 00:00, on the 1st, 10th, and 20th of every month"},
            // month constraints
            {"0 8 * 1,7 *",       "at 08:00, every day in January and July"},
            {"0 8 * 3-6 *",       "at 08:00, every day in March through June"},
            {"0 8 * */3 *",       "at 08:00, every day in January, April, July, and October"},
            // combined month + DOM
            {"0 0 1 1 *",         "at 00:00, on the 1st of January"},
            {"0 0 1 1,4,7,10 *",  "at 00:00, on the 1st of January, April, July, and October"},
            // 6-field with seconds
            {"0 0 8 * * *",       "at 08:00, every day"},
            {"0 30 8 * * 1-5",    "at 08:30, every weekday"},
            {"0 0 0 1 * *",       "at 00:00, on the 1st of every month"},
            {"0 0 0 * * 0",       "at 00:00, every Sunday"},
            {"15 30 8 * * 0,6",   "at 08:30:15, on weekends"},
        };
    }

    @Test(dataProvider = "unsupportedQuartzExpressions")
    public void unsupportedQuartzExpressionTest(String expression) {
        // given
        Exception exception = null;

        // when
        try {
            CronExpression.parse(expression);
        } catch (Exception e) {
            exception = e;
        }

        // then
        Asserts.assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test(dataProvider = "invalidFieldCountExpressions")
    public void invalidFieldCountExpressionTest(String expression) {
        // given
        Exception exception = null;

        // when
        try {
            CronExpression.parse(expression);
        } catch (Exception e) {
            exception = e;
        }

        // then
        Asserts.assertEqualsType(exception, IllegalArgumentException.class);
    }

    @DataProvider
    public Object[][] unsupportedQuartzExpressions() {
        return new Object[][] {
            {"0 0 0 ? * *"},
            {"0 0 0 L * *"},
            {"0 0 0 15W * *"},
            {"0 0 0 * * 5#2"}
        };
    }

    @DataProvider
    public Object[][] invalidFieldCountExpressions() {
        return new Object[][] {
            {"0 0 0 0"},
            {"0 0 0 0 0 0 0"}
        };
    }

    @Test(dataProvider = "nextTimeOfTimestampCases")
    public void nextTimeOfTimestampTest(
        String expression,
        LocalDateTime inputTime,
        LocalDateTime expectedTime
    ) {
        // given
        CronExpression cron = CronExpression.parse(expression);
        long timestamp = toEpochMilli(inputTime);

        // when
        long actual = cron.nextTimeOf(timestamp);

        // then
        Asserts.assertEquals(fromEpochMilli(actual), expectedTime);
    }

    @Test
    public void nextTimeOfTimestamp_impossibleExpression_returnsMinusOneTest() {
        // given
        CronExpression cron = CronExpression.parse("0 8 31 2 *");
        long timestamp = toEpochMilli(LocalDateTime.of(2026, 6, 9, 0, 0, 0));

        // when
        long actual = cron.nextTimeOf(timestamp);

        // then
        Asserts.assertEquals(actual, -1L);
    }

    @DataProvider
    public Object[][] nextTimeOfTimestampCases() {
        return new Object[][] {
            // second ceiling = null → advance to next minute
            {
                "0/30 * * * * *",
                LocalDateTime.of(2026, 6, 9, 17, 16, 30),
                LocalDateTime.of(2026, 6, 9, 17, 17, 0)
            },
            // immediate next second matches
            {
                "0/30 * * * * *",
                LocalDateTime.of(2026, 6, 9, 17, 16, 29),
                LocalDateTime.of(2026, 6, 9, 17, 16, 30)
            },
            // every minute (5-field, seconds fixed at 0)
            {
                "* * * * *",
                LocalDateTime.of(2026, 1, 5, 8, 30, 0),
                LocalDateTime.of(2026, 1, 5, 8, 31, 0)
            },
            // specific hour not yet reached → fires today
            {
                "0 8 * * *",
                LocalDateTime.of(2026, 6, 9, 7, 59, 59),
                LocalDateTime.of(2026, 6, 9, 8, 0, 0)
            },
            // specific hour already passed → advance to next day
            {
                "0 8 * * *",
                LocalDateTime.of(2026, 6, 9, 8, 0, 0),
                LocalDateTime.of(2026, 6, 10, 8, 0, 0)
            },
            // minute ceiling = null → advance to next hour
            {
                "0 45 * * *",
                LocalDateTime.of(2026, 6, 9, 17, 46, 0),
                LocalDateTime.of(2026, 6, 9, 18, 45, 0)
            },
            // hour ceiling = null → advance to next day (6-field)
            {
                "0 0 8 * * *",
                LocalDateTime.of(2026, 6, 9, 9, 0, 0),
                LocalDateTime.of(2026, 6, 10, 8, 0, 0)
            },
            // crosses month boundary
            {
                "0 0 1 7 *",
                LocalDateTime.of(2026, 6, 9, 0, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0, 0)
            },
            // crosses year boundary (no later matching month in same year)
            {
                "0 0 1 1 *",
                LocalDateTime.of(2026, 6, 9, 0, 0, 0),
                LocalDateTime.of(2027, 1, 1, 0, 0, 0)
            },
            // day-of-week constraint: Jan 4, 2026 is Sunday (cron 0)
            {
                "0 0 0 * * 0",
                LocalDateTime.of(2026, 1, 3, 23, 59, 59),
                LocalDateTime.of(2026, 1, 4, 0, 0, 0)
            },
        };
    }

    private static long toEpochMilli(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMilli),
            ZoneId.systemDefault()
        );
    }

    private static TreeSet<Integer> range(int start, int end) {
        TreeSet<Integer> answer = new TreeSet<>();
        for (int i = start; i <= end; ++i) {
            answer.add(i);
        }
        return answer;
    }

    private static TreeSet<Integer> set(Integer... values) {
        return new TreeSet<>(Arrays.asList(values));
    }
}
