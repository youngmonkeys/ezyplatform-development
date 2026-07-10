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
import org.youngmonkeys.ezyplatform.util.Hexes;

import java.lang.reflect.Constructor;

public class HexesTest {

    @Test
    public void toLowercaseHexAndDecodeTest() {
        // given
        byte[] bytes = new byte[] {
            0x00,
            0x01,
            0x0f,
            0x10,
            0x7f,
            (byte) 0x80,
            (byte) 0xff
        };

        // when
        String hex = Hexes.toLowercaseHex(bytes);
        byte[] actual = Hexes.decodeLowercaseHex(hex);

        // then
        Asserts.assertEquals(hex, "00010f107f80ff");
        Asserts.assertEquals(actual, bytes);
    }

    @Test
    public void decodeLowercaseHexWithInvalidLengthTest() {
        // given
        // when
        Throwable e = Asserts.assertThrows(
            () -> Hexes.decodeLowercaseHex("0")
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void decodeLowercaseHexWithInvalidDigitTest() {
        // given
        // when
        Throwable e = Asserts.assertThrows(
            () -> Hexes.decodeLowercaseHex("0g")
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void constructorTest() throws Exception {
        // given
        Constructor<Hexes> constructor =
            Hexes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // when
        Hexes actual = constructor.newInstance();

        // then
        Asserts.assertNotNull(actual);
    }
}
