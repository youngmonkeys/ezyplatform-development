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

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.Keywords;

import java.util.Collections;
import java.util.List;

public class KeywordsTest {

    @Test
    public void toKeywordsTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        List<String> actual = Keywords.toKeywords(str);

        // then
        List<String> expectation = Lists.newArrayList(
            "lucky wheel game",
            "lucky wheel",
            "lucky",
            "wheel",
            "game",
            "luc",
            "whe",
            "gam",
            "lu",
            "wh",
            "ga"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxLengthTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        List<String> actual = Keywords.toKeywords(str, true, 1);

        // then
        List<String> expectation = Lists.newArrayList(
            "g",
            "w",
            "l"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxWordTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        List<String> actual = Keywords.toKeywords(str, true, 12);

        // then
        List<String> expectation = Lists.newArrayList(
            "wheel game",
            "lucky wheel",
            "lucky",
            "wheel",
            "game",
            "luc",
            "whe",
            "gam",
            "lu",
            "wh",
            "ga"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxLengthIsZeroTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        List<String> actual = Keywords.toKeywords(str, true, 0);

        // then
        List<String> expectation = Lists.newArrayList(
            ""
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithNullIfEmptyTrueTest() {
        // given
        String str = "";

        // when
        List<String> actual = Keywords.toKeywords(str, true);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void toKeywordsWithNullIfEmptyFalseTest() {
        // given
        String str = "";

        // when
        List<String> actual = Keywords.toKeywords(str, false);

        // then
        Asserts.assertEquals(actual, Collections.emptyList());
    }

    @Test
    public void toKeywordsBySplash() {
        // given
        String str = "a/b/c";

        // when
        List<String> actual = Keywords.toKeywords(str, false);

        // then
        Asserts.assertEquals(
            actual,
            Lists.newArrayList(
                "a b c", "a b", "a", "b", "c"
            )
        );
    }
}
