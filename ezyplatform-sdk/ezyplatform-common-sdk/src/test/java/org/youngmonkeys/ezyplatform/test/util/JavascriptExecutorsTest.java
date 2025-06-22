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

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.JavascriptExecutors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

public class JavascriptExecutorsTest {

    @Test
    public void executeTest() {
        // given
        String script = "foo + bar";
        Map<String, Object> parameters = EzyMapBuilder
            .mapBuilder()
            .put("foo", new BigDecimal("10.2"))
            .put("bar", new BigDecimal("10.3"))
            .toMap();

        // when
        BigDecimal result1 = JavascriptExecutors
            .execute(
                script,
                parameters,
                2,
                RoundingMode.UP
            );
        BigDecimal result2 = JavascriptExecutors
            .execute(
                script,
                parameters,
                0,
                RoundingMode.UP
            );

        // then
        Asserts.assertEquals(result1, new BigDecimal("20.50"));
        Asserts.assertEquals(result2, new BigDecimal("21"));
    }

    @Test
    public void executeReturnTest() {
        // given
        String script = "if (foo < 10) { 20; } else { bar; }";
        Map<String, Object> parameters = EzyMapBuilder
            .mapBuilder()
            .put("foo", new BigDecimal("10.2"))
            .put("bar", new BigDecimal("10.3"))
            .toMap();

        // when
        BigDecimal result1 = JavascriptExecutors
            .execute(
                script,
                parameters,
                2,
                RoundingMode.UP
            );
        BigDecimal result2 = JavascriptExecutors
            .execute(
                script,
                parameters,
                0,
                RoundingMode.UP
            );

        // then
        Asserts.assertEquals(result1, new BigDecimal("10.30"));
        Asserts.assertEquals(result2, new BigDecimal("11"));
    }

    @Test
    public void executeReturnNothingTest() {
        // given
        String script = "";

        // when
        BigDecimal result = JavascriptExecutors
            .execute(
                script,
                Collections.emptyMap(),
                2,
                RoundingMode.UP
            );

        // then
        Asserts.assertNull(result);
    }

    @Test
    public void executeReturnNullTest() {
        // given
        String script = "null";

        // when
        BigDecimal result = JavascriptExecutors
            .execute(
                script,
                Collections.emptyMap(),
                2,
                RoundingMode.UP
            );

        // then
        Asserts.assertNull(result);
    }

    @Test
    public void executeReturnNullFuncTest() {
        // given
        String script = "function a() {return null;} a();";

        // when
        BigDecimal result = JavascriptExecutors
            .execute(
                script,
                Collections.emptyMap(),
                2,
                RoundingMode.UP
            );

        // then
        Asserts.assertNull(result);
    }

    @Test
    public void executeReturnNullStrTest() {
        // given
        String script = "'null'";

        // when
        BigDecimal result = JavascriptExecutors
            .execute(
                script,
                Collections.emptyMap(),
                2,
                RoundingMode.UP
            );

        // then
        Asserts.assertNull(result);
    }
}
