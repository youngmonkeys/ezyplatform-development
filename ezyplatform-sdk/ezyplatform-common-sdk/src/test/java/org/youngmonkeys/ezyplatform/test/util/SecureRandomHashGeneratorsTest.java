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
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.SecureRandomHashGenerators;

import java.lang.reflect.Constructor;

public class SecureRandomHashGeneratorsTest {

    @Test
    public void generateTest() {
        // given
        // when
        String hash = SecureRandomHashGenerators.generate(
            "accessToken",
            "admin",
            "1"
        );
        String secondHash = SecureRandomHashGenerators.generate(
            "accessToken",
            "admin",
            "1"
        );

        // then
        Asserts.assertEquals(hash.length(), 64);
        Asserts.assertTrue(hash.matches("[0-9a-f]{64}"));
        Asserts.assertTrue(secondHash.matches("[0-9a-f]{64}"));
        Asserts.assertNotEquals(hash, secondHash);
    }

    @Test
    public void generateWithNullValuesTest() {
        // given
        // when
        String hash = SecureRandomHashGenerators.generate(
            null,
            "admin",
            null
        );

        // then
        Asserts.assertEquals(hash.length(), 64);
        Asserts.assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    public void constructorTest() throws Exception {
        // given
        Constructor<SecureRandomHashGenerators> constructor =
            SecureRandomHashGenerators.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // when
        SecureRandomHashGenerators actual = constructor.newInstance();

        // then
        Asserts.assertNotNull(actual);
    }
}
