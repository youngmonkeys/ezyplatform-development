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
import org.youngmonkeys.ezyplatform.util.Hashes;

import java.lang.reflect.Constructor;

public class HashesTest {

    @Test
    public void hmacSha256Test() throws Exception {
        // given
        String key = "secret-key";
        String data = "hello world";

        // when
        String actual = Hashes.hmacSha256(key, data);

        // then
        Asserts.assertEquals(
            actual,
            "095d5a21fe6d0646db223fdf3de6436bb8dfb2fab0b51677ecf6441fcf5f2a67"
        );
    }

    @Test
    public void hmacSha256WithSameInputReturnsSameResultTest()
        throws Exception {
        // given
        String key = "secret-key";
        String data = "hello world";

        // when
        String first = Hashes.hmacSha256(key, data);
        String second = Hashes.hmacSha256(key, data);

        // then
        Asserts.assertEquals(first, second);
    }

    @Test
    public void hmacSha256WithDifferentKeyReturnsDifferentResultTest()
        throws Exception {
        // given
        String data = "hello world";

        // when
        String first = Hashes.hmacSha256("key-1", data);
        String second = Hashes.hmacSha256("key-2", data);

        // then
        Asserts.assertNotEquals(first, second);
    }

    @Test
    public void hmacSha256WithNullKeyTest() {
        // given
        // when
        Throwable e = Asserts.assertThrows(
            () -> Hashes.hmacSha256(null, "hello world")
        );

        // then
        Asserts.assertEqualsType(e, NullPointerException.class);
        Asserts.assertEquals(e.getMessage(), "hmacSha256 key is null");
    }

    @Test
    public void hmacSha256WithNullDataTest() {
        // given
        // when
        Throwable e = Asserts.assertThrows(
            () -> Hashes.hmacSha256("secret-key", null)
        );

        // then
        Asserts.assertEqualsType(e, NullPointerException.class);
        Asserts.assertEquals(e.getMessage(), "hmacSha256 data is null");
    }

    @Test
    public void constructorTest() throws Exception {
        // given
        Constructor<Hashes> constructor =
            Hashes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // when
        Hashes actual = constructor.newInstance();

        // then
        Asserts.assertNotNull(actual);
    }
}
