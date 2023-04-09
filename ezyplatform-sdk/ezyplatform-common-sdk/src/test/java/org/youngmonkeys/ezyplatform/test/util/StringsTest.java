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
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.performance.Performance;
import org.testng.annotations.Test;

import java.math.BigInteger;

import static org.youngmonkeys.ezyplatform.util.Strings.*;

public class StringsTest extends BaseTest {

    @Test
    public void test() {
        // given
        // when
        // then
        assert startsWithIgnoreSpaces("hello", "h");
        assert startsWithIgnoreSpaces("   hello", "hell");
        assert startsWithIgnoreSpaces("\thello", "hell");

        assert !startsWithIgnoreSpaces("hello", "a");
        assert !startsWithIgnoreSpaces(" \t", "a");
        assert !startsWithIgnoreSpaces(" \n", "a");
        assert !startsWithIgnoreSpaces(" hello", "hellow");
    }

    @Test
    public void containInvalidSpacesTest() {
        // given
        // when
        // then
        assert containInvalidSpaces("a\tb");
        assert containInvalidSpaces("a\nb");
        assert containInvalidSpaces("a  b");
        assert containInvalidSpaces("a b  ");
        assert containInvalidSpaces("  a b");
        assert !containInvalidSpaces("a b");
        assert !containInvalidSpaces("a b ");
        assert !containInvalidSpaces(" a b");
    }

    @Test
    public void hideSensitiveInformationTest() {
        // given
        String str = "youngmonkeys";

        // when
        String actual = hideSensitiveInformation(
            str,
            3,
            3
        );

        // then
        Asserts.assertEquals(
            actual,
            "you***onkeys"
        );
    }

    @Test
    public void hideSensitiveInformationWithLargeStartLengthTest() {
        // given
        String str = "young";

        // when
        String actual = hideSensitiveInformation(
            str,
            5,
            3
        );

        // then
        Asserts.assertEquals(
            actual,
            "young"
        );
    }

    @Test
    public void hideSensitiveInformationWithLargeLengthTest() {
        // given
        String str = "youngmonkeys";

        // when
        String actual = hideSensitiveInformation(
            str,
            3,
            100
        );

        // then
        Asserts.assertEquals(
            actual,
            "you***"
        );
    }

    @Test
    public void emptyIfNullTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            emptyIfNull("hello"),
            "hello"
        );
        Asserts.assertEquals(
            emptyIfNull(null),
            ""
        );
    }

    @Test
    public void toBigIntegerOrZeroTest() {
        Asserts.assertEquals(
            toBigIntegerOrZero("1"),
            BigInteger.ONE
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("1.1"),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("true"),
            BigInteger.ZERO
        );

        //noinspection ResultOfMethodCallIgnored
        long time = Performance
            .create()
            .loop(10000)
            .test(() -> toBigIntegerOrZero("hello world"))
            .getTime();
        System.out.println("toBigIntegerOrZero elapsed time: " + time);
    }
}
