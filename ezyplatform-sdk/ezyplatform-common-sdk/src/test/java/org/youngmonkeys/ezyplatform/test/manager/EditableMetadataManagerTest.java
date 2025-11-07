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

package org.youngmonkeys.ezyplatform.test.manager;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.manager.EditableMetadataManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditableMetadataManagerTest {

    @Test
    public void test() {
        // given
        EditableMetadataManager instance = new EditableMetadataManager();
        String name1 = RandomUtil.randomShortAlphabetString();
        String type1 = RandomUtil.randomShortAlphabetString();
        String defaultValue1 = RandomUtil.randomShortAlphabetString();
        String name2 = RandomUtil.randomShortAlphabetString();
        String type2 = RandomUtil.randomShortAlphabetString();
        String defaultValue2 = RandomUtil.randomShortAlphabetString();
        instance.addMetadataNameAndType(name1, type1);
        instance.addMetadataNameAndTypeMap(
            Collections.singletonMap(name2, type2)
        );
        instance.setMetadataDefaultValue(name1, defaultValue1);
        instance.setMetadataDefaultValues(
            Collections.singletonMap(name2, defaultValue2)
        );

        // when
        // then
        Asserts.assertEquals(
            instance.getMetadataTypeByName(name1),
            type1
        );

        Asserts.assertEquals(
            instance.getMetadataDefaultValueByName(
                name1
            ),
            defaultValue1
        );

        Asserts.assertEquals(
            instance.getMetadataNames(),
            Sets.newHashSet(name1, name2)
        );

        Asserts.assertEquals(
            new HashSet<>(instance.getMetadataTypeMessageKeys()),
            Sets.newHashSet(type1.toLowerCase(), type2.toLowerCase())
        );

        Asserts.assertEquals(
            instance.getSortedMetadataNames(),
            Stream.of(name1.toLowerCase(), name2.toLowerCase())
                .sorted()
                .collect(Collectors.toList())
        );

        Asserts.assertEquals(
            instance.getMetadataNameTypeMap(),
            EzyMapBuilder.mapBuilder()
                .put(name1, type1)
                .put(name2, type2)
                .toMap(),
            false
        );

        Asserts.assertEquals(
            instance.getDefaultMetadataValueMap(),
            EzyMapBuilder.mapBuilder()
                .put(name1, defaultValue1)
                .put(name2, defaultValue2)
                .toMap(),
            false
        );
    }
}
