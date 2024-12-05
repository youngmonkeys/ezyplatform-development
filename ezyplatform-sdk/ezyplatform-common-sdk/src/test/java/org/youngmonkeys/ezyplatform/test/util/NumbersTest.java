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

package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.Numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.youngmonkeys.ezyplatform.util.Numbers.*;

public class NumbersTest {

    @Test
    public void toIntOrZeroTest() {
        Asserts.assertEquals(toIntOrZero("10"), 10);
        Asserts.assertEquals(toIntOrZero("10a"), 0);
    }

    @Test
    public void toLongOrZeroTest() {
        Asserts.assertEquals(toLongOrZero("10"), 10L);
        Asserts.assertEquals(toLongOrZero("10a"), 0L);
    }

    @Test
    public void roundUpOrDownToIntTest() {
        // given
        double a = 1.1;
        double b = 2.5;
        double c = 3.49999999999;
        double d = 4.50000000001;

        // when
        // then
        Asserts.assertEquals(
            roundUpOrDownToInt(a),
            1
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(b),
            3
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(c),
            3
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(d),
            5
        );
    }

    @Test
    public void formatToUnitStringTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            formatToUnitString(1, 1),
            "1"
        );
        Asserts.assertEquals(
            formatToUnitString(1000, 1),
            "1K"
        );
        Asserts.assertEquals(
            formatToUnitString(1100, 1),
            "1.1K"
        );
        Asserts.assertEquals(
            formatToUnitString(1_000_000, 1),
            "1M"
        );
        Asserts.assertEquals(
            formatToUnitString(1_100_000, 1),
            "1.1M"
        );
        Asserts.assertEquals(
            formatToUnitString(1_000_000_000, 1),
            "1B"
        );
        Asserts.assertEquals(
            formatToUnitString(1_100_000_000, 1),
            "1.1B"
        );
    }

    @Test
    public void toIntValueTest() {
        Asserts.assertEquals(
            toIntValue(BigInteger.TEN),
            10
        );
        Asserts.assertEquals(
            toIntValue(null),
            0
        );
    }

    @Test
    public void toPaddedValueTest() {
        Asserts.assertEquals(
            toPaddedValueLong(BigInteger.TEN),
            "00000000000000000000000010"
        );
        Asserts.assertEquals(
            toPaddedValueLong(null, BigInteger.ONE),
            "00000000000000000000000001"
        );
        Asserts.assertEquals(
            toPaddedValueUint256(BigInteger.TEN),
            "0000000000000000000000000000000000000000000000000000000000000000000000000010"
        );
        Asserts.assertEquals(
            toPaddedValueUint256(null, BigInteger.ONE),
            "0000000000000000000000000000000000000000000000000000000000000000000000000001"
        );
    }

    @Test
    public void toNoTrailingZerosStringTest() {
        Asserts.assertEquals(
            toNoTrailingZerosString(BigDecimal.ZERO),
            "0"
        );
        Asserts.assertEquals(
            toNoTrailingZerosString(
                new BigDecimal(String.valueOf(Long.MAX_VALUE))
            ),
            String.valueOf(Long.MAX_VALUE)
        );
        Asserts.assertEquals(
            toNoTrailingZerosString(new BigDecimal("25.00000")),
            "25"
        );
        Asserts.assertEquals(
            toNoTrailingZerosString(new BigDecimal("0.02500")),
            "0.025"
        );
        Asserts.assertEquals(
            toNoTrailingZerosString(new BigDecimal("-25.00000")),
            "-25"
        );
    }

    @Test
    public void toRandomTextMin8TextTest() {
        // given
        // when
        String text1 = toRandomText(12345, 8);
        String text2 = toRandomText(1234567, 8);
        String text3 = toRandomText(123456789, 8);

        // then
        Asserts.assertTrue(text1.startsWith("2"));
        Asserts.assertTrue(text1.endsWith("12345"));
        Asserts.assertEquals(text2.length(), 8);

        Asserts.assertTrue(text2.startsWith("0"));
        Asserts.assertTrue(text2.endsWith("1234567"));
        Asserts.assertEquals(text2.length(), 8);

        Asserts.assertTrue(text3.startsWith("0"));
        Asserts.assertTrue(text3.endsWith("123456789"));
        Asserts.assertEquals(text3.length(), 10);
    }

    @Test
    public void toRandomTextMin12Test() {
        // given
        // when
        String text1 = toRandomText(1, 12);
        String text2 = toRandomText(1234567, 12);
        String text3 = toRandomText(11123456789L, 12);
        String text4 = toRandomText(111123456789L, 12);

        // then
        Asserts.assertTrue(text1.startsWith("9"));
        Asserts.assertTrue(text1.endsWith("01"));
        Asserts.assertEquals(text2.length(), 12);

        Asserts.assertTrue(text2.startsWith("4"));
        Asserts.assertTrue(text2.endsWith("1234567"));
        Asserts.assertEquals(text2.length(), 12);

        Asserts.assertTrue(text3.startsWith("0"));
        Asserts.assertTrue(text3.endsWith("11123456789"));
        Asserts.assertEquals(text3.length(), 12);

        Asserts.assertTrue(text4.startsWith("0"));
        Asserts.assertTrue(text4.endsWith("111123456789"));
        Asserts.assertEquals(text4.length(), 13);
    }

    @Test
    public void fromRandomTextTest() {
        // given
        // when
        long id1 = fromRandomText("21212345");
        long id2 = fromRandomText("01234567");
        long id3 = fromRandomText("0123456789");
        long id4 = fromRandomText("912345678901");

        // then
        Asserts.assertEquals(id1, 12345L);
        Asserts.assertEquals(id2, 1234567L);
        Asserts.assertEquals(id3, 123456789L);
        Asserts.assertEquals(id4, 1L);
    }
}
