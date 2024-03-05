/*
 * Copyright 2024 youngmonkeys.org
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
import org.youngmonkeys.ezyplatform.util.Versions;

public class VersionsTest {

    @Test
    public void compareVersionsTest() {
        Asserts.assertEquals(
            Versions.compareVersions(null, null),
            0
        );
        Asserts.assertEquals(
            Versions.compareVersions("1.0.0", null),
            1
        );
        Asserts.assertEquals(
            Versions.compareVersions(null, "1.0.0"),
            -1
        );
        Asserts.assertEquals(
            Versions.compareVersions("1.0.0", "0.0.1"),
            1
        );
        Asserts.assertEquals(
            Versions.compareVersions("1.0.0", "1.0.0"),
            0
        );
        Asserts.assertEquals(
            Versions.compareVersions("1.0.0", "2.0.0"),
            -1
        );
        Asserts.assertEquals(
            Versions.compareVersions("1.0.9", "1.0.10"),
            -1
        );
    }
}
