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
public class ConfigurationPropertiesManager extends EzyLoggable {

    private final ObjectMapper objectMapper;
    private final FileSystemManager fileSystemManager;

    private static final String FILE_CONFIG_SOCKET =
        "socket/settings/config.properties";
    private static final String FILE_SETTINGS_SOCKET =
        "socket/settings/ezy-settings.json";

    public Map<String, Object> getSocketConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        readProperties().forEach((key, value) ->
            configuration.put(key.toString(), value)
        );
        configuration.putAll(readJsonSettings());
        return configuration;
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
}
