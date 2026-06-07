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

import java.time.LocalDateTime;
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
