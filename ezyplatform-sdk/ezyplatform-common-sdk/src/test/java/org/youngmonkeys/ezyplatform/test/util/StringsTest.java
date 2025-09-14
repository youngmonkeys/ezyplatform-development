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
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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
        String text = RandomUtil.randomShortAlphabetString();

        // when
        // then
        Asserts.assertEquals(
            emptyIfNull(text),
            text
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
        String str = "hello !\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~ world \t I'm\nhere–hello";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(str);

        // then
        Asserts.assertEquals(actual,"hello-world-im-here-hello");
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
    public void toDashLowerCaseWithoutSpecialCharactersFirstTest() {
        // given
        String str = "--Thong bao: Cap Nhat";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(str);

        // then
        Asserts.assertEquals(actual,"-thong-bao-cap-nhat");
    }

    @Test
    public void toDashLowerCaseWithoutSpecialCharactersDoubleQuoteTest() {
        // given
        String str = "Thong bao: \"Cap Nhat\"";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(str);

        // then
        Asserts.assertEquals(actual,"thong-bao-cap-nhat");
    }

    @Test
    public void toDashLowerCaseWithoutSpecialCharactersWithFilterTest() {
        // given
        String str = "Thong bao: \"Cap Nhat\"";

        // when
        String actual = toLowerDashCaseWithoutSpecialCharacters(
            str,
            ch -> ch != 'n' && ch != 'N'
        );

        // then
        Asserts.assertEquals(actual,"thog-bao-cap-hat");
    }

    @Test
    public void toBigIntegerOrZeroTest() {
        Asserts.assertEquals(
            toBigIntegerOrZero("1"),
            BigInteger.ONE
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("-1"),
            new BigInteger("-1")
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("1.1"),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("true"),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero("-"),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero(null),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero(""),
            BigInteger.ZERO
        );
        Asserts.assertEquals(
            toBigIntegerOrZero(" "),
            BigInteger.ZERO
        );

        long time = Performance
            .create()
            .test(() -> toBigIntegerOrZero("hello world"))
            .getTime();
        System.out.println("toBigIntegerOrZero elapsed time: " + time);
    }

    @Test
    public void toBigDecimalOrZeroTest() {
        Asserts.assertEquals(
            toBigDecimalOrZero("1"),
            BigDecimal.ONE
        );
        Asserts.assertEquals(
            toBigDecimalOrZero("-1"),
            new BigDecimal("-1")
        );
        Asserts.assertEquals(
            toBigDecimalOrZero("1.1"),
            new BigDecimal("1.1")
        );
        Asserts.assertEquals(
            toBigDecimalOrZero("true"),
            BigDecimal.ZERO
        );
        Asserts.assertEquals(
            toBigDecimalOrZero("-"),
            BigDecimal.ZERO
        );
        Asserts.assertEquals(
            toBigDecimalOrZero("1.1.1"),
            BigDecimal.ZERO
        );
        Asserts.assertEquals(
            toBigDecimalOrZero(null),
            BigDecimal.ZERO
        );
        Asserts.assertEquals(
            toBigDecimalOrZero(""),
            BigDecimal.ZERO
        );
        Asserts.assertEquals(
            toBigDecimalOrZero(" "),
            BigDecimal.ZERO
        );

        long time = Performance
            .create()
            .test(() -> toBigDecimalOrZero("hello world"))
            .getTime();
        System.out.println("toBigDecimalOrZero elapsed time: " + time);
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
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello ${vocative} ${who} 1\n2${vocative ? br : }3\t4 ${foo ? bar : x} 5 ${foo ?} 6",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung")
                    .put("br", "\n")
                    .toMap()
            ),
            "hello Mr Dzung 1\n2\n3\t4 x 5 6"
        );
        Asserts.assertEquals(
            fromTemplateAndParameters(
                "hello ${vocative} ${who} 1\n2${vocative ? [br] : }3\t4 ${foo ? bar : x} 5 ${foo ?} 6",
                EzyMapBuilder.mapBuilder()
                    .put("vocative", "Mr")
                    .put("who", "Dzung")
                    .put("[br]", "\n")
                    .toMap()
            ),
            "hello Mr Dzung 1\n2\n3\t4 x 5 6"
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

    @Test
    public void indexOfTextInStringIgnoreCaseTest() {
        // given
        String text = "YMSxxx5xxxtvd12xxxORDER123";

        // when
        int index1 = indexOfTextInStringIgnoreCase(
            text,
            "YMS"
        );
        int index2 = indexOfTextInStringIgnoreCase(
            text,
            "xxx",
            3
        );
        int index3 = indexOfTextInStringIgnoreCase(
            text,
            "order",
            6
        );

        // then
        Asserts.assertEquals(index1, 0);
        Asserts.assertEquals(index2, 3);
        Asserts.assertEquals(index3, 18);
    }

    @Test
    public void translateEmptyCheckTernaryOperatorIfNeedTest() {
        String result1 = translateEmptyCheckTernaryOperatorIfNeed(
            null,
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result1.isEmpty());

        String result2 = translateEmptyCheckTernaryOperatorIfNeed(
            "",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result2.isEmpty());

        String result3 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result3.isEmpty());

        String result4 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result4.isEmpty());

        String result5 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world :",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result5.isEmpty());

        String result6 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello : world ? abc",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertTrue(result6.isEmpty());

        String result7 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world : abc ",
            EzyMapBuilder.mapBuilder()
                .toMap()
        );
        Asserts.assertEquals(result7, "abc");

        String result8 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world : abc ",
            EzyMapBuilder.mapBuilder()
                .put("hello", "yes")
                .toMap()
        );
        Asserts.assertEquals(result8, "world");

        String result9 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world : abc ",
            EzyMapBuilder.mapBuilder()
                .put("hello", "yes")
                .put("world", "foo")
                .toMap()
        );
        Asserts.assertEquals(result9, "foo");

        String result10 = translateEmptyCheckTernaryOperatorIfNeed(
            "hello ? world : abc ",
            EzyMapBuilder.mapBuilder()
                .put("hello", "")
                .put("abc", "bar")
                .toMap()
        );
        Asserts.assertEquals(result10, "bar");
    }

    @Test
    public void extractVariableNameOrNullTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            extractVariableNameOrNull(" ${hello}  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" ${hello  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" ~{hello}  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" ~{hello  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" {{hello}}  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" {{hello}  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" {{hello  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" {{h}"),
            "h"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" {{h"),
            "h"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull(" hello  "),
            "hello"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull("h"),
            "h"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull("$h"),
            "$h"
        );
        Asserts.assertEquals(
            extractVariableNameOrNull("{h"),
            "{h"
        );
        Asserts.assertNull(
            extractVariableNameOrNull("   ")
        );
        Asserts.assertNull(
            extractVariableNameOrNull("${}")
        );
        Asserts.assertNull(
            extractVariableNameOrNull("${")
        );
        Asserts.assertEquals(
            extractVariableNameOrNull("$"),
            "$"
        );
        Asserts.assertNull(
            extractVariableNameOrNull("{{}}")
        );
        Asserts.assertNull(
            extractVariableNameOrNull("{{}")
        );
        Asserts.assertNull(
            extractVariableNameOrNull("{{")
        );
        Asserts.assertEquals(
            extractVariableNameOrNull("{"),
            "{"
        );
    }

    @Test
    public void isIntegerTest() {
        // given
        // when
        // then
        Asserts.assertTrue(isInteger("1"));
        Asserts.assertTrue(isInteger("123"));
        Asserts.assertTrue(isInteger("+1"));
        Asserts.assertTrue(isInteger("-11"));
        Asserts.assertFalse(isInteger("1.1"));
        Asserts.assertTrue(isInteger(BigInteger.valueOf(Long.MAX_VALUE).toString()));
        Asserts.assertTrue(isInteger(BigInteger.valueOf(Long.MIN_VALUE).toString()));
        Asserts.assertFalse(isInteger("abc"));
        Asserts.assertFalse(isInteger(""));
    }

    @Test
    public void isBigDecimalTest() {
        // given
        // when
        // then
        Asserts.assertTrue(isBigDecimal("1"));
        Asserts.assertTrue(isBigDecimal("123"));
        Asserts.assertTrue(isBigDecimal("+1"));
        Asserts.assertTrue(isBigDecimal("-11"));
        Asserts.assertTrue(isBigDecimal("1.1"));
        Asserts.assertTrue(isBigDecimal(BigInteger.valueOf(Long.MAX_VALUE).toString()));
        Asserts.assertTrue(isBigDecimal(BigInteger.valueOf(Long.MIN_VALUE).toString()));
        Asserts.assertTrue(isBigDecimal(BigDecimal.valueOf(Double.MAX_VALUE).toString()));
        Asserts.assertTrue(isBigDecimal(BigDecimal.valueOf(Double.MIN_VALUE).toString()));
        Asserts.assertFalse(isBigDecimal("abc"));
        Asserts.assertFalse(isBigDecimal(""));
    }
}
