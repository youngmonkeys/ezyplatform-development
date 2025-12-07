/*
 * Copyright 2023 youngmonkeys.org
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

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.Next;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static org.youngmonkeys.ezyplatform.util.Randoms.*;

public class RandomsTest {

    @Test
    public void randomNameTest() {
        // given
        // when
        String displayName = randomName();

        // then
        Asserts.assertEquals(displayName.length(), 16);
        System.out.println(displayName);
    }

    @Test
    public void randomEmailFromUrlTest() {
        // given
        // when
        String actual1 = randomEmailFromUrl(
            "http://localhost:8080"
        );
        String actual2 = randomEmailFromUrl(
            "http://hello.com"
        );

        // then
        Asserts.assertTrue(actual1.endsWith("localhost.com"));
        Asserts.assertTrue(actual2.endsWith("hello.com"));
    }

    @Test
    public void randomSkipLimitTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            randomSkipLimit(3, 3),
            Next.skipLimit(0, 3)
        );
        Asserts.assertEquals(
            randomSkipLimit(2, 3),
            Next.skipLimit(0, 3)
        );
        Asserts.assertTrue(
            Sets.newHashSet(
                0L,
                1L
            ).contains(randomSkipLimit(3, 2).getSkip())
        );
        Asserts.assertEquals(
            randomSkipLimit(3, 2).getLimit(),
            2L
        );
    }
}
