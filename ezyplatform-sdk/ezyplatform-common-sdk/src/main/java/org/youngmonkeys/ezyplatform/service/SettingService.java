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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import org.youngmonkeys.ezyplatform.util.Uris;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

@SuppressWarnings("MethodCount")
public interface SettingService {

    void watchLastUpdatedTime(
        String settingName,
        int periodInSecond,
        Runnable onLastUpdatedTimeChange
    );

    String getDecryptionValue(String settingName);

    <T> T getCachedValue(String settingName, T defaultValue);

    Optional<String> getSettingValue(String settingName);

    long getMaxUploadFileSize();

    Set<String> getAcceptedMediaMimeTypes();

    String decryptValue(String encryptedSettingValue);

    <T> T getObjectValue(String settingName, Class<T> objectType);

    default String getPasswordValue(String settingName) {
        return getDecryptionValue(settingName);
    }

    default boolean getBooleanValue(String settingName) {
        return getBooleanValue(settingName, false);
    }

    default boolean getBooleanValue(String settingName, boolean defaultValue) {
        return getSettingValue(settingName)
            .map(it -> {
                try {
                    return Boolean.parseBoolean(it);
                } catch (Exception e) {
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }

    default int getIntValue(String settingName) {
        return getIntValue(settingName, 0);
    }

    default int getIntValue(String settingName, int defaultValue) {
        return (int) getLongValue(settingName, defaultValue);
    }

    default long getLongValue(String settingName) {
        return getLongValue(settingName, 0L);
    }

    default long getLongValue(String settingName, long defaultValue) {
        return getSettingValue(settingName)
            .map(it -> {
                try {
                    return Long.parseLong(it);
                } catch (Exception e) {
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }

    default String getTextValue(String settingName) {
        return getTextValue(settingName, null);
    }

    default String getTextValue(String settingName, String defaultValue) {
        return getSettingValue(settingName)
            .orElse(defaultValue);
    }

    default <T> T getArrayValue(String settingName, Class<T> arrayType) {
        String textValue = getTextValue(settingName);
        if (textValue == null) {
            return null;
        }
        try {
            return SingletonStringDeserializer.getInstance()
                .deserialize(textValue, arrayType);
        } catch (Exception e) {
            return null;
        }
    }

    default Set<String> getSetStringValue(String settingName) {
        return getSetValue(settingName, String.class);
    }

    default Set<String> getSetStringValue(
        String settingName,
        Set<String> defaultValue
    ) {
        Set<String> value = getSetValue(settingName, String.class);
        return value.isEmpty() ? defaultValue : value;
    }

    @SuppressWarnings("unchecked")
    default <T> Set<T> getSetValue(String settingName, Class<T> valueType) {
        String textValue = getTextValue(settingName);
        if (textValue == null) {
            return Collections.emptySet();
        }
        try {
            return SingletonStringDeserializer
                .getInstance()
                .deserialize(textValue, Set.class, valueType);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> getListValue(String settingName, Class<T> valueType) {
        String textValue = getTextValue(settingName);
        if (textValue == null) {
            return Collections.emptyList();
        }
        try {
            return SingletonStringDeserializer
                .getInstance()
                .deserialize(textValue, List.class, valueType);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    default String getAdminUrl() {
        return getTextValue(
            SETTING_NAME_ADMIN_URL,
            DEFAULT_ADMIN_URL
        );
    }

    default String getWebUrl() {
        return getTextValue(SETTING_NAME_WEB_URL, DEFAULT_WEB_URL);
    }

    default String getWebManagementUrl() {
        return getTextValue(
            SETTING_NAME_WEB_MANAGEMENT_URL,
            DEFAULT_WEB_MANAGEMENT_URL
        );
    }

    default String getWebsocketUrl() {
        return getTextValue(SETTING_NAME_WEBSOCKET_URL, DEFAULT_WEBSOCKET_URL);
    }

    default long getUserTokenExpiredTimeInDay() {
        return getLongValue(
            SETTING_NAME_USER_TOKEN_EXPIRED_IN_DAY,
            DEFAULT_TOKEN_EXPIRED_IN_DAY
        );
    }

    default boolean containSetting(String settingName) {
        return getSettingValue(settingName)
            .isPresent();
    }

    default String resolveAdminUri(String uri) {
        return Uris.resolveUrl(getAdminUrl(), uri);
    }

    default String resolveWebUri(String uri) {
        return Uris.resolveUrl(getWebUrl(), uri);
    }

    default String resolveWebManagementUri(String uri) {
        return Uris.resolveUrl(getWebManagementUrl(), uri);
    }
}
