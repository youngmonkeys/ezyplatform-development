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
            "ucky wheel game",
            "cky wheel game",
            "ky wheel game",
            "y wheel game",
            "wheel game",
            "heel game",
            "eel game",
            "el game",
            "l game",
            "game",
            "ame",
            "me",
            "e"
        );

        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywords2Test() {
        // given
        String str = "EzyArticle";

        // when
        List<String> actual = Keywords.toKeywords(str);

        // then
        List<String> expectation = Lists.newArrayList(
            "ezyarticle",
          "zyarticle",
          "yarticle",
          "article",
          "rticle",
          "ticle",
          "icle",
          "cle",
          "le",
          "e"
        );

        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywords3Test() {
        // given
        String str = "我喜欢学习人工智能";

        // when
        List<String> actual = Keywords.toKeywords(str);

        // then
        List<String> expectation = Lists.newArrayList(
            "我喜欢学习人工智能",
            "喜欢学习人工智能",
            "欢学习人工智能",
            "学习人工智能",
            "习人工智能",
            "人工智能",
            "工智能",
            "智能",
            "能"
        );

        Asserts.assertEquals(actual, expectation);
    }

    @Test
    public void toKeywords4Test() {
        // given
        String str = "0123456789 0123456789 0123456789 0123456789";

        // when
        List<String> actual = Keywords.toKeywords(str);

        // then
        List<String> expectation = Lists.newArrayList(
            "0123456789 0123456789 01234567",
            "123456789 0123456789 012345678",
            "23456789 0123456789 0123456789",
            "3456789 0123456789 0123456789",
            "456789 0123456789 0123456789 0",
            "56789 0123456789 0123456789 01",
            "6789 0123456789 0123456789 012",
            "789 0123456789 0123456789 0123",
            "89 0123456789 0123456789 01234",
            "9 0123456789 0123456789 012345",
            "0123456789 0123456789 01234567",
            "123456789 0123456789 012345678",
            "23456789 0123456789 0123456789",
            "3456789 0123456789 0123456789",
            "456789 0123456789 0123456789",
            "56789 0123456789 0123456789",
            "6789 0123456789 0123456789",
            "789 0123456789 0123456789",
            "89 0123456789 0123456789",
            "9 0123456789 0123456789",
            "0123456789 0123456789",
            "123456789 0123456789",
            "23456789 0123456789",
            "3456789 0123456789",
            "456789 0123456789",
            "56789 0123456789",
            "6789 0123456789",
            "789 0123456789",
            "89 0123456789",
            "9 0123456789",
            "0123456789",
            "123456789",
            "23456789",
            "3456789",
            "456789",
            "56789",
            "6789",
            "789",
            "89",
            "9"
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
            "l",
            "u",
            "c",
            "k",
            "y",
            "w",
            "h",
            "e",
            "e",
            "l",
            "g",
            "a",
            "m",
            "e"
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
            "lucky wheel",
            "ucky wheel g",
            "cky wheel ga",
            "ky wheel gam",
            "y wheel game",
            "wheel game",
            "heel game",
            "eel game",
            "el game",
            "l game",
            "game",
            "ame",
            "me",
            "e"
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
        List<String> expectation = Collections.emptyList();
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
                "a/b/c",
                "/b/c",
                "b/c",
                "/c",
                "c"
            )
        );
    }
}
