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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.io.PropertiesFileProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
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

    @Test
    public void escapePropertyValueAsciiTest() {
        // given
        String value = "Node A-1 http://10.0.0.5:9090/path?a=1&b=2";

        // when
        String actual = PropertiesFileProxy.escapePropertyValue(value);

        // then
        Asserts.assertEquals(actual, value);
    }

    @Test
    public void escapePropertyValueNonAsciiTest() {
        // given
        String value = "Node A – Hà Nội";

        // when
        String actual = PropertiesFileProxy.escapePropertyValue(value);

        // then
        Asserts.assertEquals(
            actual,
            "Node A \\u2013 H\\u00e0 N\\u1ed9i"
        );
    }

    @Test
    public void escapePropertyValueBackslashAndControlCharsTest() {
        // given
        String value = "a\\b\nc\td\r";

        // when
        String actual = PropertiesFileProxy.escapePropertyValue(value);

        // then
        Asserts.assertEquals(
            actual,
            "a\\\\b\\u000ac\\u0009d\\u000d"
        );
    }

    @Test
    public void escapePropertyValueSurrogatePairTest() {
        // given
        String value = "🚀";

        // when
        String actual = PropertiesFileProxy.escapePropertyValue(value);

        // then
        Asserts.assertEquals(actual, "\\ud83d\\ude80");
    }

    @Test
    public void escapePropertyValueRoundTripTest() throws Exception {
        // given
        String name = "Node A – Hà Nội \\ 東京 🚀\nserver.ip=1.2.3.4";
        Map<String, String> properties = new HashMap<>();
        properties.put("server.ip", "10.0.0.5");
        properties.put(
            "server.name",
            PropertiesFileProxy.escapePropertyValue(name)
        );
        File file = new File("test-escape-properties.properties");

        // when
        PropertiesFileProxy.sortAndWrite(properties, file);

        // then
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            for (byte b : bytes) {
                Asserts.assertTrue(b >= 0);
            }
            Properties actual = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                actual.load(inputStream);
            }
            Asserts.assertEquals(actual.size(), 2);
            Asserts.assertEquals(actual.getProperty("server.name"), name);
            Asserts.assertEquals(actual.getProperty("server.ip"), "10.0.0.5");
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }
}
