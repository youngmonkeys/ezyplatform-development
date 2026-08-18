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

package org.youngmonkeys.ezyplatform.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyLoggable;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class ConfigurationPropertiesManager extends EzyLoggable {

    private final SocketPropertiesManager socketPropertiesManager;

    public ConfigurationPropertiesManager(
        FileSystemManager fileSystemManager,
        ObjectMapper objectMapper
    ) {
        socketPropertiesManager = new SocketPropertiesManager(
            fileSystemManager,
            objectMapper
        );
    }

    public Map<String, Object> getSocketConfiguration() {
        return socketPropertiesManager
            .getSocketConfiguration();
    }

    public int getTcpPort() {
        return socketPropertiesManager
            .getTcpPort();
    }

    public int getUdpPort() {
        return socketPropertiesManager
            .getUdpPort();
    }

    public int getWebsocketPort() {
        return socketPropertiesManager
            .getWebsocketPort();
    }

    public int getSslWebsocketPort() {
        return socketPropertiesManager
            .getSslWebsocketPort();
    }

    public <T> T getSocketConfigValue(
        String propertyName,
        String jsonFieldName,
        T defaultValue
    ) {
        return socketPropertiesManager
            .getSocketConfigValue(
                propertyName,
                jsonFieldName,
                defaultValue
            );
    }
}
