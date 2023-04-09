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

import com.tvd12.test.assertion.Asserts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.util.ReflectionObjects.getObjectProperties;
import static org.youngmonkeys.ezyplatform.util.ReflectionObjects.isEmptyObject;

public class ReflectionObjectsTest {

    @Test
    public void isEmptyObjectTest() {
        Asserts.assertTrue(
            isEmptyObject(new A())
        );
        Asserts.assertFalse(
            isEmptyObject(new B("hello"))
        );
    }

    @Test
    public void getObjectPropertiesTest() {
        // given
        C c = new C("Hello", "World");

        // when
        Map<String, Object> actual = getObjectProperties(
            c
        );

        // then
        Map<String, Object> expectation = new HashMap<>();
        expectation.put("hello", "Hello");
        expectation.put("world", "World");
        Asserts.assertEquals(actual, expectation);
    }

    public static class A {}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class B extends A {
        private String hello;
    }

    @Getter
    @Setter
    public static class C extends B {

        public String world;

        public C(String hello, String world) {
            super(hello);
            this.world = world;
        }
    }
}
