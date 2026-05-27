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
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.SqlFileVersionExtractors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;

public class SqlFileVersionExtractorsTest extends BaseTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                null
            ),
            ZERO
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello"
            ),
            ZERO
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "123"
            ),
            ZERO
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello-123.sql"
            ),
            123
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello_123.sql"
            ),
            123
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello123.sql"
            ),
            ZERO
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello.sql"
            ),
            ZERO
        );
        Asserts.assertEquals(
            SqlFileVersionExtractors.extractVersion(
                "hello.sql-123"
            ),
            ZERO
        );
    }
}
