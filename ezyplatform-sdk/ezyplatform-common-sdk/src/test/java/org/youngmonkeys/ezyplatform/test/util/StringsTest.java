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

import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyfox.util.EzyEntry;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.performance.Performance;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static org.youngmonkeys.ezyplatform.util.Strings.*;

public class StringsTest extends BaseTest {

    public static void main(String[] args) {
        System.out.println(Arrays.toString("abc".split("\\|\\|")));
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "\t{{vocative}}\t${}",
                EzyMapBuilder.mapBuilder()
                    .toMap()
            ),
            ""
        );
    }

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
    public void fromTest() {
        Asserts.assertEquals(from(null), null);
        Asserts.assertEquals(from("Hello"), "Hello");
    }

    @Test
    public void entryToStringTest() {
        Asserts.assertEquals(
            entryToString(EzyEntry.of("Hello", null)),
            "Hello="
        );
        Asserts.assertEquals(
            entryToString(EzyEntry.of("Hello", "World")),
            "Hello=World"
        );
    }

    @Test
    public void toLowerDashCaseTest() {
        // given
        String value = "Hello World";

        // when
        String actual = toLowerDashCase(value);
        String actual2 = toLowerDashCase("");

        // then
        Asserts.assertEquals(actual, "hello-world");
        Asserts.assertEquals(actual2, "");
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
    public void substringTest() {
        Asserts.assertEquals(
            substring("hello", -1, 6),
            "hello"
        );
        Asserts.assertEquals(
            substring("hello", 1, 3),
            "el"
        );
    }

    @Test
    public void substringLastTest() {
        Asserts.assertEquals(
            substringLast("hello", 3, 2),
            "el"
        );

        Asserts.assertEquals(
            substringLast("hello", 3, 5),
            "hel"
        );
    }

    @Test
    public void endsWithTest() {
        Asserts.assertTrue(
            endsWith("hello", 3, "hel")
        );
        Asserts.assertFalse(
            endsWith("hello", 3, "hol")
        );
        Asserts.assertFalse(
            endsWith("hello", 3, "hell")
        );
        Asserts.assertFalse(
            endsWith("hello", 1, "he")
        );
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
    public void emptyIfBlankTest() {
        Asserts.assertEquals(
            emptyIfBlank("hello"),
            "hello"
        );
        Asserts.assertEquals(
            emptyIfBlank(null),
            ""
        );
        Asserts.assertEquals(
            emptyIfBlank(""),
            ""
        );
        Asserts.assertEquals(
            emptyIfBlank(" "),
            ""
        );
        Asserts.assertEquals(
            emptyIfBlank("\t"),
            ""
        );
        Asserts.assertEquals(
            emptyIfBlank("\n"),
            ""
        );
    }

    @Test
    public void toDashLowerCaseWithoutSpecialCharactersTest() {
        // given
        String str = "hello !\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~ world \t I'm\nhere";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(str);

        // then
        Asserts.assertEquals(actual,"hello---world---im-here");
    }

    @Test
    public void toDashLowerCaseWithoutSpecialCharactersColonTest() {
        // given
        String str = "Thong bao: Cap Nhat";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(str);

        // then
        Asserts.assertEquals(actual,"thong-bao-cap-nhat");
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

    @Test
    public void firstNotBlankValueTest() {
        Asserts.assertEquals(
            firstNotBlankValue("", "a"),
            "a"
        );
        Asserts.assertNull(firstNotBlankValue("", ""));
    }

    @Test
    public void fromTemplateAndParametersTest() {
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello ${vocative} ${who} 1\n2 3\t4",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "Dzung")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello ${vocative}\t \t \t \t${who} 1\n2 3\t\t\t4",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "Dzung")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello {{vocative}}\t \t \t \t{{who}} 1\n2 3\t\t\t4",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "Dzung")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello {{vocative}}\t \t \t \t{{who}} 1\n2 3\t\t\t4",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "\tDzung ")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello {{vocative}}\t \t \t \t{{who}} 1\n2 3\t\t\t4    ",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "\tDzung ")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello {{vocative}}\t \t \t \t{{who}} 1\n2 3\t\t\t4\t\t",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", " ")
                    .put("who", "\tDzung ")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4"
        );

        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello {{vocative}}\t \t \t \t{{who}} 1\n2 3\t\t\t4\t\t { z {{abc",
                EzyMapBuilder.mapBuilder()
                    .put("who", "\tDzung ")
                    .toMap()
            ),
            "hello Dzung 1\n2 3\t4\t{ z {{abc"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "{{vocative}} ${who}",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung ")
                    .toMap()
            ),
            "Mr Dzung"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "{{vocative}} ${who}}",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung ")
                    .toMap()
            ),
            "Mr Dzung}"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "  {{vocative}} ${}",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung ")
                    .toMap()
            ),
            "Mr"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "\t{{vocative}}\t${}",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung ")
                    .toMap()
            ),
            "Mr"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "\t{{vocative}}\t${}",
                EzyMapBuilder.mapBuilder()
                    .toMap()
            ),
            ""
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "{{vocative}",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .toMap()
            ),
            "{{vocative}"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "${vocative",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .toMap()
            ),
            "${vocative"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "${vocative} ${who} a",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .toMap()
            ),
            "Mr a"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "${vocative}\t${who}\ta",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .toMap()
            ),
            "Mr\ta"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "${vocative}${who} a",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .toMap()
            ),
            "Mr a"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "${vocative}",
                EzyMapBuilder.mapBuilder()
                    .toMap()
            ),
            ""
        );
    }

    @Test
    public void toDateOrTimeOrDateTimeStringTest() {
        LocalDateTime time = LocalDateTime.of(
            2025, 1, 1, 1, 1, 1
        );
        Asserts.assertEquals(
            toDateOrTimeOrDateTimeString(time.toLocalDate(), "yyyy-MM-dd"),
            "2025-01-01"
        );
        Asserts.assertEquals(
            toDateOrTimeOrDateTimeString(time.toLocalTime(), "HH:mm:ss"),
            "01:01:01"
        );
        Asserts.assertEquals(
            toDateOrTimeOrDateTimeString(EzyDates.toInstant(time).toEpochMilli(), "yyyy-MM-dd HH:mm:ss"),
            "2025-01-01 01:01:01"
        );
        Asserts.assertEquals(
            toDateOrTimeOrDateTimeString(EzyDates.toInstant(time), "yyyy-MM-dd HH:mm:ss"),
            "2025-01-01 01:01:01"
        );
        Asserts.assertEquals(
            toDateOrTimeOrDateTimeString("Hello", "yyyy-MM-dd"),
            "Hello"
        );
    }

    @Test
    public void setDateTimeToVariableIfNeedTest() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(
            2025, 1, 1, 1, 1, 1
        );

        // when
        Object actual1 = setDateTimeToVariableIfNeed(
            null,
            null
        );
        Object actual2 = setDateTimeToVariableIfNeed(
            "Hello",
            null
        );
        Object actual3 = setDateTimeToVariableIfNeed(
            "time||Date||yyyy-MM-dd",
            EzyMapBuilder.mapBuilder().toMap()
        );
        Object actual4 = setDateTimeToVariableIfNeed(
            "time||Date||yyyy-MM-dd",
            EzyMapBuilder.mapBuilder()
                .put("time", dateTime)
                .toMap()
        );
        Object actual5 = setDateTimeToVariableIfNeed(
            "time||Time||HH:mm:ss",
            EzyMapBuilder.mapBuilder()
                .put("time", dateTime)
                .toMap()
        );
        Object actual6 = setDateTimeToVariableIfNeed(
            "time||DateTime||yyyy-MM-dd HH:mm:ss",
            EzyMapBuilder.mapBuilder()
                .put("time", dateTime)
                .toMap()
        );
        Object actual7 = setDateTimeToVariableIfNeed(
            "Hello||Hello||yyyy-MM-dd HH:mm:ss",
            EzyMapBuilder.mapBuilder()
                .put("Hello", dateTime)
                .toMap()
        );
        Object actual8 = setDateTimeToVariableIfNeed(
            "time||DateTime",
            EzyMapBuilder.mapBuilder()
                .put("time", dateTime)
                .toMap()
        );
        Object actual9 = setDateTimeToVariableIfNeed(
            "time",
            EzyMapBuilder.mapBuilder()
                .put("time", dateTime)
                .toMap()
        );

        // then
        Asserts.assertEquals(actual1, EMPTY_STRING);
        Asserts.assertEquals(actual2, EMPTY_STRING);
        Asserts.assertEquals(actual3, EMPTY_STRING);
        Asserts.assertEquals(actual4, "2025-01-01");
        Asserts.assertEquals(actual5, "01:01:01");
        Asserts.assertEquals(actual6, "2025-01-01 01:01:01");
        Asserts.assertEquals(actual7, dateTime.toString());
        Asserts.assertEquals(actual8, dateTime.toString());
        Asserts.assertEquals(actual9, dateTime.toString());
    }
}
