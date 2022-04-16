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

package org.youngmonkeys.ezyplatform.test.data;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.performance.Performance;
import lombok.Data;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.PaginationParameter;

import java.util.Map;

public class ParameterMapFetcherTest {

    public static void main(String[] args) {
        AParameterListFetcher fetcher = new AParameterListFetcher();
        long elapsedTime = Performance.create()
            .test(fetcher::getParameters)
            .getTime();
        System.out.println(elapsedTime);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        // given
        AParameterListFetcher sut = new AParameterListFetcher();

        // when
        Map<String, Object> actual = sut.getParameters();

        // then
        Map<String, Object> expectation = EzyMapBuilder.mapBuilder()
            .put("hello", 1)
            .put("world", "a")
            .put("foo", "x")
            .put("bar", "y")
            .build();
        Asserts.assertEquals(actual, expectation, false);
    }

    @Data
    public static class AParameterListFetcher implements PaginationParameter {
        private long hello = 1;
        private String world = "a";
        public String foo = "x";
        private String bar = "y";
        public final String v = "v";
        public static String z = "z";
        public transient String t = "t";
    }
}
