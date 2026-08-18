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
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.manager.SocketPropertiesManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class SocketPropertiesManagerTest {

    private Path ezyHome;
    private SocketPropertiesManager instance;

    private static final int DEFAULT_TCP_PORT = 3005;
    private static final int DEFAULT_UDP_PORT = 2611;
    private static final int DEFAULT_WEBSOCKET_PORT = 2208;
    private static final int DEFAULT_SSL_WEBSOCKET_PORT = 2812;

    @BeforeMethod
    public void setup() throws Exception {
        ezyHome = Files.createTempDirectory("socket-properties-manager-");
        FileSystemManager fileSystemManager = () -> ezyHome.toString();
        instance = new SocketPropertiesManager(
            fileSystemManager,
            new ObjectMapper()
        );
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
    public void getPortsShouldReturnDefaultsWhenNoFileExists() {
        // given
        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), DEFAULT_TCP_PORT);
        Asserts.assertEquals(instance.getUdpPort(), DEFAULT_UDP_PORT);
        Asserts.assertEquals(
            instance.getWebsocketPort(),
            DEFAULT_WEBSOCKET_PORT
        );
        Asserts.assertEquals(
            instance.getSslWebsocketPort(),
            DEFAULT_SSL_WEBSOCKET_PORT
        );
        Asserts.assertEmpty(instance.getSocketConfiguration());
    }

    @Test
    public void getPortsShouldReadFromPropertiesFile() throws Exception {
        // given
        writeProperties(
            "socket.port=3006\n"
                + "udp.port=2612\n"
                + "websocket.port=2209\n"
                + "websocket.ssl.port=2813\n"
        );

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 3006);
        Asserts.assertEquals(instance.getUdpPort(), 2612);
        Asserts.assertEquals(instance.getWebsocketPort(), 2209);
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2813);
    }

    @Test
    public void getPortsShouldReadFromJsonSettingsFile() throws Exception {
        // given
        writeJsonSettings(
            "{\n"
                + "  \"socket\": {\"port\": 3006},\n"
                + "  \"udp\": {\"port\": 2612},\n"
                + "  \"websocket\": {\"port\": 2209, \"sslPort\": 2813}\n"
                + "}"
        );

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 3006);
        Asserts.assertEquals(instance.getUdpPort(), 2612);
        Asserts.assertEquals(instance.getWebsocketPort(), 2209);
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2813);
    }

    @Test
    public void jsonSettingsShouldHaveHigherPriorityThanProperties()
        throws Exception {
        // given
        writeProperties("socket.port=3006\nudp.port=2612\n");
        writeJsonSettings("{\"socket\": {\"port\": 4006}}");

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 4006);
        Asserts.assertEquals(instance.getUdpPort(), 2612);
    }

    @Test
    public void jsonFieldNameShouldBeMatchedIgnoreCaseAndSeparators()
        throws Exception {
        // given
        writeJsonSettings(
            "{\"WEB_SOCKET\": {\"ssl-port\": 2813, \"PORT\": 2209}}"
        );

        // when
        // then
        Asserts.assertEquals(instance.getSslWebsocketPort(), 2813);
        Asserts.assertEquals(instance.getWebsocketPort(), 2209);
    }

    @Test
    public void jsonFieldValueShouldBeNullWhenPathIsNotAMap() throws Exception {
        // given
        writeProperties("socket.port=3006\n");
        writeJsonSettings("{\"socket\": 4006}");

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), 3006);
    }

    @Test
    public void getPortsShouldReturnDefaultsWhenJsonFileIsBlank()
        throws Exception {
        // given
        writeJsonSettings("   \n  ");

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), DEFAULT_TCP_PORT);
        Asserts.assertEmpty(instance.getSocketConfiguration());
    }

    @Test
    public void getPortsShouldReturnDefaultsWhenJsonFileIsInvalid()
        throws Exception {
        // given
        writeJsonSettings("{invalid-json");

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), DEFAULT_TCP_PORT);
        Asserts.assertEmpty(instance.getSocketConfiguration());
    }

    @Test
    public void getPortShouldReturnDefaultWhenValueIsNotANumber()
        throws Exception {
        // given
        writeProperties("socket.port=not-a-number\n");

        // when
        // then
        Asserts.assertEquals(instance.getTcpPort(), DEFAULT_TCP_PORT);
    }

    @Test
    public void getSocketConfigurationShouldMergePropertiesAndJsonSettings()
        throws Exception {
        // given
        writeProperties("socket.port=3006\nsocket.debug=true\n");
        writeJsonSettings(
            "{\"socket.port\": 4006, \"socket.maxRequestSize\": 1024}"
        );

        // when
        Map<String, Object> configuration = instance.getSocketConfiguration();

        // then
        Asserts.assertEquals(configuration.get("socket.port"), 4006);
        Asserts.assertEquals(configuration.get("socket.debug"), "true");
        Asserts.assertEquals(configuration.get("socket.maxRequestSize"), 1024);
        Asserts.assertEquals(configuration.size(), 3);
    }

    @Test
    public void getSocketConfigValueShouldConvertStringToTargetType()
        throws Exception {
        // given
        writeProperties(
            "socket.maxRequestSize=1024\n"
                + "socket.enable=true\n"
                + "socket.name=socket-server\n"
        );

        // when
        // then
        Asserts.assertEquals(
            instance.getSocketConfigValue("socket.maxRequestSize", "n/a", 1L),
            1024L
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue("socket.enable", "n/a", false),
            true
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue("socket.name", "n/a", ""),
            "socket-server"
        );
    }

    @Test
    public void getSocketConfigValueShouldConvertNumberToTargetType()
        throws Exception {
        // given
        writeJsonSettings("{\"socket\": {\"maxRequestSize\": 1024}}");

        // when
        // then
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "n/a",
                "socket.maxRequestSize",
                1L
            ),
            1024L
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "n/a",
                "socket.maxRequestSize",
                1
            ),
            1024
        );
        Asserts.assertEquals(
            instance.getSocketConfigValue(
                "n/a",
                "socket.maxRequestSize",
                ""
            ),
            "1024"
        );
    }

    @Test
    public void getSocketConfigValueShouldReturnRawValueWhenDefaultIsNull()
        throws Exception {
        // given
        writeJsonSettings("{\"socket\": {\"host\": \"127.0.0.1\"}}");

        // when
        Object value = instance.getSocketConfigValue(
            "n/a",
            "socket.host",
            null
        );

        // then
        Asserts.assertEquals(value, "127.0.0.1");
    }

    @Test
    public void getSocketConfigValueShouldReturnDefaultWhenTypeUnsupported()
        throws Exception {
        // given
        writeProperties("socket.rate=1.5\n");

        // when
        double value = instance.getSocketConfigValue(
            "socket.rate",
            "n/a",
            2.5D
        );

        // then
        Asserts.assertEquals(value, 2.5D);
    }

    @Test
    public void getSocketConfigValueShouldReturnDefaultWhenValueNotFound() {
        // given
        // when
        // then
        Asserts.assertEquals(
            instance.getSocketConfigValue("socket.host", "socket.host", "any"),
            "any"
        );
    }

    private void writeProperties(String content) throws Exception {
        writeSettingsFile("config.properties", content);
    }

    private void writeJsonSettings(String content) throws Exception {
        writeSettingsFile("ezy-settings.json", content);
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
