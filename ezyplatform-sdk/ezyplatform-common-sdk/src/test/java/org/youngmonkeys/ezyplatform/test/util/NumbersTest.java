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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static org.youngmonkeys.ezyplatform.util.Numbers.roundUpOrDownToInt;

public class NumbersTest {

    @Test
    public void roundUpOrDownToIntTest() {
        // given
        double a = 1.1;
        double b = 2.5;
        double c = 3.49999999999;
        double d = 4.50000000001;

        // when
        // then
        Asserts.assertEquals(
            roundUpOrDownToInt(a),
            1
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(b),
            3
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(c),
            3
        );
        Asserts.assertEquals(
            roundUpOrDownToInt(d),
            5
        );
    }
}
