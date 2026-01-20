/*
 * Copyright 2026 youngmonkeys.org
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.ProxyObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyObjectTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertFalse(new ProxyObject<>(
            new Person("1", "a")
        ).equals(null));
    }

    @Test
    public void testOneField() {
        // given
        List<Person> persons = Arrays.asList(
            new Person("1", "a"),
            new Person("2", "b"),
            new Person("1", "c")
        );

        // when
        List<Person> actual = persons
            .stream()
            .map(it ->
                new ProxyObject<>(it)
                    .equalFieldValueExtractor(Person::getName)
            )
            .distinct()
            .map(ProxyObject::getValue)
            .collect(Collectors.toList());

        // then
        Asserts.assertEquals(
            actual,
            Arrays.asList(
                new Person("1", "a"),
                new Person("2", "b")
            ),
            false
        );
    }

    @Test
    public void testTwoField() {
        // given
        List<Person> persons = Arrays.asList(
            new Person("1", "a"),
            new Person("2", "b"),
            new Person("1", "c"),
            new Person("1", "a")
        );

        // when
        List<Person> actual = persons
            .stream()
            .map(it ->
                new ProxyObject<>(it)
                    .equalFieldValueExtractor(Person::getName)
                    .equalFieldValueExtractor(Person::getDisplayName)
            )
            .distinct()
            .map(ProxyObject::getValue)
            .collect(Collectors.toList());

        // then
        Asserts.assertEquals(
            actual,
            Arrays.asList(
                new Person("1", "a"),
                new Person("2", "b"),
                new Person("1", "c")
            ),
            false
        );
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Person {
        private String name;
        private String displayName;
    }
}
