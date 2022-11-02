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

import com.tvd12.test.performance.Performance;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalAndBigIntegerTest {

    public static void main(String[] args) {
        long bigDecimalTime = Performance
            .create()
            .test(() -> new BigDecimal("10").add(BigDecimal.ONE))
            .getTime();
        System.out.println(bigDecimalTime);
        long bigIntegerTime = Performance
            .create()
            .test(() -> new BigInteger("10").add(BigInteger.ONE))
            .getTime();
        System.out.println(bigIntegerTime);

        System.out.println(new BigDecimal(new BigInteger("10")).toBigInteger());
    }
}
