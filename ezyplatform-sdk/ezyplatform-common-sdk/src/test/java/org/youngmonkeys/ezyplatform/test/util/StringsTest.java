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

import org.testng.annotations.Test;

import static org.youngmonkeys.ezyplatform.util.Strings.containInvalidSpaces;
import static org.youngmonkeys.ezyplatform.util.Strings.startsWithIgnoreSpaces;

public class StringsTest {

    @Test
    public void test() {
        // given
        // when
        // then
        assert startsWithIgnoreSpaces("hello", "h");
        assert startsWithIgnoreSpaces("   hello", "hell");
        assert startsWithIgnoreSpaces("\thello", "hell");

        assert !startsWithIgnoreSpaces("hello", "a");
        assert !startsWithIgnoreSpaces(" \t", "a");
        assert !startsWithIgnoreSpaces(" \n", "a");
        assert !startsWithIgnoreSpaces(" hello", "hellow");
    }

    @Test
    public void containInvalidSpacesTest() {
        // given
        // when
        // then
        assert containInvalidSpaces("a\tb");
        assert containInvalidSpaces("a\nb");
        assert containInvalidSpaces("a  b");
        assert containInvalidSpaces("a b  ");
        assert containInvalidSpaces("  a b");
        assert !containInvalidSpaces("a b");
        assert !containInvalidSpaces("a b ");
        assert !containInvalidSpaces(" a b");
    }
}
