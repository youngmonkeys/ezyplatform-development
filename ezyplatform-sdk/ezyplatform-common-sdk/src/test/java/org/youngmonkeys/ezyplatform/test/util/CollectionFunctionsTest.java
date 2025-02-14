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

import com.tvd12.ezyfox.util.EzyEntry;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.youngmonkeys.ezyplatform.util.CollectionFunctions.distinctByField;

public class CollectionFunctionsTest {

    @Test
    public void distinctByFieldTest() {
        // given
        EzyEntry<String, Integer> entry1 = EzyEntry.of("a", 1);
        EzyEntry<String, Integer> entry2 = EzyEntry.of("b", 1);
        EzyEntry<String, Integer> entry3 = EzyEntry.of("c", 2);
        EzyEntry<String, Integer> entry4 = EzyEntry.of("d", 2);

        List<EzyEntry<String, Integer>> collection = Arrays.asList(
            entry1,
            entry2,
            entry3,
            entry4
        );

        // when
        List<EzyEntry<String, Integer>> actual =
            distinctByField(collection, EzyEntry::getValue);

        // then
        Asserts.assertEquals(
            actual,
            Arrays.asList(entry1, entry3),
            false
        );
    }
}
