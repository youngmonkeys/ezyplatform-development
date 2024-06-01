/*
 * Copyright 2024 youngmonkeys.org
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

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

public class StreamTest {

    @Test
    public void testEmpty() {
        // given
        // when
        String actual = Stream.of(null, "")
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.joining(", "));

        // then
        Asserts.assertEquals(actual, EMPTY_STRING);
    }

    @Test
    public void testNull() {
        // given
        // when
        String actual = Stream.of((String) null, (String) null)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.joining(", "));

        // then
        Asserts.assertEquals(actual, EMPTY_STRING);
    }
}
