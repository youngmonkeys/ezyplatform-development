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
import org.testng.collections.Sets;
import org.youngmonkeys.ezyplatform.util.Keywords;

import java.util.Collections;
import java.util.Set;

public class KeywordsTest {

    @Test
    public void toKeywordsTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        Set<String> actual = Keywords.toKeywords(str);

        // then
        Set<String> expectation = Sets.newHashSet(
            "lucky",
            "wheel",
            "game",
            "lucky wheel",
            "lucky wheel game"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxLengthTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        Set<String> actual = Keywords.toKeywords(str, true, 1);

        // then
        Set<String> expectation = Sets.newHashSet(
            "l",
            "w",
            "g"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxWordTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        Set<String> actual = Keywords.toKeywords(str, true, 12);

        // then
        Set<String> expectation = Sets.newHashSet(
            "lucky",
            "wheel",
            "game",
            "lucky wheel",
            "wheel game"
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithMaxLengthIsZeroTest() {
        // given
        String str = "Lucky Wheel Game";

        // when
        Set<String> actual = Keywords.toKeywords(str, true, 0);

        // then
        Set<String> expectation = Sets.newHashSet(
            ""
        );
        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywordsWithNullIfEmptyTrueTest() {
        // given
        String str = "";

        // when
        Set<String> actual = Keywords.toKeywords(str, true);

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void toKeywordsWithNullIfEmptyFalseTest() {
        // given
        String str = "";

        // when
        Set<String> actual = Keywords.toKeywords(str, false);

        // then
        Asserts.assertEquals(actual, Collections.emptySet());
    }
}
