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
import com.tvd12.properties.file.reader.BaseFileReader;
import lombok.AllArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

@AllArgsConstructor
public class SocketPropertiesManager extends EzyLoggable {

    private final FileSystemManager fileSystemManager;
    private final ObjectMapper objectMapper;

    private static final String FILE_CONFIG_SOCKET =
        "socket/settings/config.properties";
    private static final String FILE_SETTINGS_SOCKET =
        "socket/settings/ezy-settings.json";

    private static final String PROPERTY_TCP_PORT = "socket.port";
    private static final String PROPERTY_UDP_PORT = "udp.port";
    private static final String PROPERTY_WEBSOCKET_PORT = "websocket.port";
    private static final String PROPERTY_SSL_WEBSOCKET_PORT =
        "websocket.ssl.port";

    private static final String JSON_FIELD_TCP_PORT = "socket.port";
    private static final String JSON_FIELD_UDP_PORT = "udp.port";
    private static final String JSON_FIELD_WEBSOCKET_PORT = "websocket.port";
    private static final String JSON_FIELD_SSL_WEBSOCKET_PORT =
        "websocket.sslPort";

    private static final int DEFAULT_TCP_PORT = 3005;
    private static final int DEFAULT_UDP_PORT = 2611;
    private static final int DEFAULT_WEBSOCKET_PORT = 2208;
    private static final int DEFAULT_SSL_WEBSOCKET_PORT = 2812;

    public Map<String, Object> getSocketConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        readProperties().forEach((key, value) ->
            configuration.put(key.toString(), value)
        );
        configuration.putAll(readJsonSettings());
        return configuration;
    }

    public int getTcpPort() {
        return getSocketConfigValue(
            PROPERTY_TCP_PORT,
            JSON_FIELD_TCP_PORT,
            DEFAULT_TCP_PORT
        );
    }

    public int getUdpPort() {
        return getSocketConfigValue(
            PROPERTY_UDP_PORT,
            JSON_FIELD_UDP_PORT,
            DEFAULT_UDP_PORT
        );
    }

    public int getWebsocketPort() {
        return getSocketConfigValue(
            PROPERTY_WEBSOCKET_PORT,
            JSON_FIELD_WEBSOCKET_PORT,
            DEFAULT_WEBSOCKET_PORT
        );
    }

    public int getSslWebsocketPort() {
        return getSocketConfigValue(
            PROPERTY_SSL_WEBSOCKET_PORT,
            JSON_FIELD_SSL_WEBSOCKET_PORT,
            DEFAULT_SSL_WEBSOCKET_PORT
        );
    }

    public <T> T getSocketConfigValue(
        String propertyName,
        String jsonFieldName,
        T defaultValue
    ) {
        Object value = getJsonFieldValue(
            readJsonSettings(),
            jsonFieldName
        );
        if (value == null) {
            value = readProperties().get(propertyName);
        }
        return convertValue(value, defaultValue);
    }

    private Properties readProperties() {
        File propertiesFile = fileSystemManager
            .concatWithEzyHomeToFile(FILE_CONFIG_SOCKET);
        if (!propertiesFile.exists()) {
            logger.info("not found properties file: {}", propertiesFile);
            return new Properties();
        }
        return new BaseFileReader().read(propertiesFile);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonSettings() {
        File jsonFile = fileSystemManager.concatWithEzyHomeToFile(
            FILE_SETTINGS_SOCKET
        );
        if (!jsonFile.exists()) {
            logger.info("not found json file: {}", jsonFile);
            return new HashMap<>();
        }
        try {
            String content = new String(
                Files.readAllBytes(jsonFile.toPath()),
                StandardCharsets.UTF_8
            );
            if (isBlank(content)) {
                return new HashMap<>();
            }
            Map<String, Object> settings = objectMapper.readValue(
                content,
                Map.class
            );
            return settings == null ? new HashMap<>() : settings;
        } catch (Exception e) {
            logger.warn("read json file: {} failed", jsonFile, e);
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Object getJsonFieldValue(
        Map<String, Object> settings,
        String jsonFieldName
    ) {
        Object value = settings;
        for (String fieldName : jsonFieldName.split("\\.")) {
            if (!(value instanceof Map)) {
                return null;
            }
            value = getFieldValue(
                (Map<String, Object>) value,
                fieldName
            );
            if (value == null) {
                return null;
            }
        }
        return value;
    }

    private Object getFieldValue(
        Map<String, Object> settings,
        String fieldName
    ) {
        Object value = settings.get(fieldName);
        if (value != null) {
            return value;
        }
        String normalizedFieldName = normalizeFieldName(fieldName);
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            if (normalizeFieldName(entry.getKey()).equals(normalizedFieldName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String normalizeFieldName(String fieldName) {
        return fieldName
            .toLowerCase()
            .replace("-", "")
            .replace("_", "");
    }

    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (defaultValue == null) {
            return (T) value;
        }
        Class<?> defaultValueType = defaultValue.getClass();
        if (defaultValueType.isInstance(value)) {
            return (T) value;
        }
        String stringValue = value.toString().trim();
        try {
            if (defaultValueType == Integer.class) {
                return (T) (Integer) (
                    value instanceof Number
                        ? ((Number) value).intValue()
                        : Integer.parseInt(stringValue)
                    );
            }
            if (defaultValueType == Long.class) {
                return (T) (Long) (
                    value instanceof Number
                        ? ((Number) value).longValue()
                        : Long.parseLong(stringValue)
                    );
            }
            if (defaultValueType == Boolean.class) {
                return (T) Boolean.valueOf(stringValue);
            }
            if (defaultValueType == String.class) {
                return (T) stringValue;
            }
        } catch (Exception e) {
            logger.warn(
                "can not convert value: {} to type: {}",
                value,
                defaultValueType,
                e
            );
        }
        return defaultValue;
    }
}
