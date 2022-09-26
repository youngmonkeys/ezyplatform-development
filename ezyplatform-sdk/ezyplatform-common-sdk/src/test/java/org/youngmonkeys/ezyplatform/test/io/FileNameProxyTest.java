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

package org.youngmonkeys.ezyplatform.test.io;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.io.FileNameProxy;

public class FileNameProxyTest {

    @Test
    public void extractVersionTest() {
        // given
        String fileName = "ezymail-1.0.0.zip";

        // when
        String actual = FileNameProxy.extractVersion(fileName);

        // then
        Asserts.assertEquals(actual, "1.0.0");
    }
}
