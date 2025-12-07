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

package org.youngmonkeys.ezyplatform.test.io;

import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.io.PropertiesFileProxy;

import java.io.File;
import java.util.Properties;

public class PropertiesFileProxyTest {

    @Test
    public void sortAndWriteTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("b", "2");
        properties.setProperty("a", "1");
        File file = new File("test-properties.properties");

        // when
        PropertiesFileProxy.sortAndWrite(
            properties,
            file
        );

        // then
        System.out.println(file.delete());
    }
}
