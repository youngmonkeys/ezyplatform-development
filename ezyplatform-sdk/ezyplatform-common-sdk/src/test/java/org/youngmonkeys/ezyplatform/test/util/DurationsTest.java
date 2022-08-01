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
import org.youngmonkeys.ezyplatform.util.Durations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DurationsTest {

    @Test
    public void test() {
        // given
        List<String> input = Arrays.asList(
            "10ms",
            "100s",
            "1000m",
            "10000h",
            "100000d",
            "1000000w",
            "10000000M",
            "100000000y",
            "1ms22s333m",
            "1",
            "ms",
            "1abc"
        );

        // when
        List<Long> actual = input
            .stream()
            .map(Durations::durationStringToMillis)
            .collect(Collectors.toList());

        // then
        List<Long> expectation = Arrays.asList(
            10L,
            100 * 1000L,
            1000 * 60 * 1000L,
            10000 * 60 * 60 * 1000L,
            100000L * 24 * 60 * 60 * 1000L,
            1000000L * 7 * 24 * 60 * 60 * 1000L,
            10000000L * 30 * 24 * 60 * 60 * 1000L,
            100000000L * 365 * 24 * 60 * 60 * 1000L,
            1 + 22 * 1000 +  333 * 60 * 1000L,
            -1L,
            -1L,
            -1L
        );
        Asserts.assertEquals(actual, expectation, false);
    }
}
