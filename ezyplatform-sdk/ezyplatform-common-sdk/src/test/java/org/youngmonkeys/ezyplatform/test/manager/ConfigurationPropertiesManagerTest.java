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

package org.youngmonkeys.ezyplatform.test.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.manager.ConfigurationPropertiesManager;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.manager.SocketPropertiesManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationPropertiesManagerTest {

    private Path ezyHome;

    @BeforeMethod
    public void setup() throws Exception {
        ezyHome = Files.createTempDirectory("configuration-properties-manager-");
    }

    @AfterMethod
    public void tearDown() throws Exception {
        try (Stream<Path> paths = Files.walk(ezyHome)) {
            paths.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    public void getPortsShouldReturnDefaultsWhenNoSocketFileExists() {
        // given
        ConfigurationPropertiesManager instance = newInstance();

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 3005);
        Asserts.assertEquals(instance.getUdpPort(), 2611);
        Asserts.assertEquals(instance.getWebsocketPort(), 2208);
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2812);
        Asserts.assertEmpty(instance.getSocketConfiguration());
    }

    @Test
    public void getPortsShouldReadFromSocketPropertiesFile() throws Exception {
        // given
        writeSettingsFile(
            "config.properties",
            "socket.port=3006\n"
                + "udp.port=2612\n"
                + "websocket.port=2209\n"
                + "websocket.ssl.port=2813\n"
        );
        ConfigurationPropertiesManager instance = newInstance();

        // when
        Map<String, Object> configuration = instance.getSocketConfiguration();

        // then
        Asserts.assertEquals(instance.getTcpPort(), 3006);
        Asserts.assertEquals(instance.getUdpPort(), 2612);
        Asserts.assertEquals(instance.getWebsocketPort(), 2209);
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2813);
        Asserts.assertEquals(configuration.get("socket.port"), "3006");
        Asserts.assertEquals(configuration.size(), 4);
    }

    @Test
    public void getPortsShouldReadFromSocketJsonSettingsFile()
        throws Exception {
        // given
        writeSettingsFile(
            "ezy-settings.json",
            "{\"socket\": {\"port\": 4006}}"
        );
        ConfigurationPropertiesManager instance = newInstance();

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 4006);
        Asserts.assertEquals(instance.getUdpPort(), 2611);
    }

    @Test
    public void getSocketConfigValueShouldReadFromSocketSettings()
        throws Exception {
        // given
        writeSettingsFile(
            "config.properties",
            "socket.maxRequestSize=1024\n"
        );
        writeSettingsFile(
            "ezy-settings.json",
            "{\"socket\": {\"host\": \"127.0.0.1\"}}"
        );
        ConfigurationPropertiesManager instance = newInstance();

        // when
        // then
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "socket.maxRequestSize",
                "socket.maxRequestSize",
                1L
            ),
            1024L
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "socket.host",
                "socket.host",
                "localhost"
            ),
            "127.0.0.1"
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue("socket.debug", "socket.debug", true),
            true
        );
    }

    @Test
    public void shouldReturnValuesFromSocketPropertiesManager() {
        // given
        SocketPropertiesManager socketPropertiesManager = mock(
            SocketPropertiesManager.class
        );
        Map<String, Object> socketConfiguration = Collections.singletonMap(
            "socket.port",
            3006
        );
        when(socketPropertiesManager.getSocketConfiguration())
            .thenReturn(socketConfiguration);
        when(socketPropertiesManager.getTcpPort()).thenReturn(3006);
        when(socketPropertiesManager.getUdpPort()).thenReturn(2612);
        when(socketPropertiesManager.getWebsocketPort()).thenReturn(2209);
        when(socketPropertiesManager.getSslWebsocketPort()).thenReturn(2813);
        when(
            socketPropertiesManager.getSocketConfigValue(
                "socket.host",
                "socket.host",
                "localhost"
            )
        ).thenReturn("127.0.0.1");
        ConfigurationPropertiesManager instance =
            new ConfigurationPropertiesManager(socketPropertiesManager);

        // when
        // then
        Asserts.assertEquals(
            instance.getSocketConfiguration(),
            socketConfiguration
        );
        Asserts.assertEquals(instance.getTcpPort(), 3006);
        Asserts.assertEquals(instance.getUdpPort(), 2612);
        Asserts.assertEquals(instance.getWebsocketPort(), 2209);
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2813);
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "socket.host",
                "socket.host",
                "localhost"
            ),
            "127.0.0.1"
        );
    }

    private ConfigurationPropertiesManager newInstance() {
        FileSystemManager fileSystemManager = () -> ezyHome.toString();
        return new ConfigurationPropertiesManager(
            fileSystemManager,
            new ObjectMapper()
        );
    }

    private void writeSettingsFile(
        String fileName,
        String content
    ) throws Exception {
        Path settingsFolder = Paths.get(
            ezyHome.toString(),
            "socket",
            "settings"
        );
        Files.createDirectories(settingsFolder);
        Files.write(
            Paths.get(settingsFolder.toString(), fileName),
            content.getBytes(StandardCharsets.UTF_8)
        );
    }
}
