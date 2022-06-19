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
            "Lucky",
            "wheel",
            "Wheel",
            "game",
            "Game",
            "lucky wheel",
            "Lucky Wheel",
            "lucky wheel game",
            "Lucky Wheel Game"
        );
        Asserts.assertEquals(actual, expectation);
    }
}
