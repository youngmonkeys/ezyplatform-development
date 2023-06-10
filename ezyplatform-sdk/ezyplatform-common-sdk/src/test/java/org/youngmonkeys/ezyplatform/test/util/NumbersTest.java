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
}
