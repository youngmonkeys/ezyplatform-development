/*
 * Copyright 2025 youngmonkeys.org
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

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.Maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void mergeTest() {
        // given
        // when
        Map<String, Object> actual1 = Maps.merge(
            null,
            null
        );
        Map<String, Object> actual2 = Maps.merge(
            Collections.singletonMap("hello", "world"),
            null
        );
        Map<String, Object> actual3 = Maps.merge(
            Collections.singletonMap("hello", "world"),
            Collections.singletonMap("foo", "bar")
        );


        // then
        Asserts.assertEquals(actual1, new HashMap<>(), false);
        Asserts.assertEquals(
            actual2,
            Collections.singletonMap("hello", "world"),
            false
        );
        Asserts.assertEquals(
            actual3,
            EzyMapBuilder.mapBuilder()
                .put("hello", "world")
                .put("foo", "bar")
                .toMap(),
            false
        );
    }
}
