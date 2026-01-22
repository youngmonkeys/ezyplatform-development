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

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyfox.function.EzyExceptionFunction;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.ezyhttp.core.util.FileSizes;
import org.youngmonkeys.ezyplatform.entity.DataType;
import org.youngmonkeys.ezyplatform.util.Uris;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.util.Randoms.randomEmailFromUrl;

@SuppressWarnings("MethodCount")
public interface SettingService {

    int DEFAULT_CACHE_PERIOD_IN_SECOND = 5;

    void watchLastUpdatedTime(
        String settingName,
        int periodInSecond,
        Runnable onLastUpdatedTimeChange
    );

    default void watchLastUpdatedTime(
        String settingName,
        Runnable onLastUpdatedTimeChange
    ) {
        watchLastUpdatedTime(
            settingName,
            DEFAULT_CACHE_PERIOD_IN_SECOND,
            onLastUpdatedTimeChange
        );
    }

    void watchLastUpdatedTimeAndCache(
        String settingName,
        int periodInSecond
    );

    default void watchLastUpdatedTimeAndCache(
        String settingName
    ) {
        watchLastUpdatedTimeAndCache(
            settingName,
            DEFAULT_CACHE_PERIOD_IN_SECOND
        );
    }

    void addValueConverter(
        String settingName,
        EzyExceptionFunction<String, Object> converter
    );

    void scheduleCacheValue(
        String settingName,
        int periodInSecond
    );

    default void scheduleCacheValue(String settingName) {
        scheduleCacheValue(
            settingName,
            DEFAULT_CACHE_PERIOD_IN_SECOND
        );
    }

    default void saveSetting(
        String name,
        DataType dataType,
        Object value
    ) {
        saveSetting(name, dataType.toString(), value);
    }

    default void saveSetting(
        String name,
        String dataType,
        Object value
    ) {
        throw new EzyNotImplementedException(
            "saveSetting has not implemented yet"
        );
    }

    String getDecryptionValue(String settingName);

    default <T> T getCachedValue(String settingName) {
        return getCachedValue(settingName, null);
    }

    <T> T getCachedValue(String settingName, T defaultValue);

    Optional<String> getSettingValue(String settingName);

    default long getMaxUploadFileSize() {
        String value = getTextValue(
            SETTING_NAME_WEB_MAX_UPLOAD_FILE_SIZE,
            DEFAULT_MAX_UPLOAD_FILE_SIZE
        );
        try {
            return FileSizes.toByteSize(value);
        } catch (Exception e) {
            return FileSizes.toByteSize(DEFAULT_MAX_UPLOAD_FILE_SIZE);
        }
    }

    default Set<String> getAcceptedMediaMimeTypes() {
        return getCachedValue(
            SETTING_NAME_WEB_ACCEPTED_MEDIA_MIME_TYPES,
            DEFAULT_ACCEPTED_IMAGE_TYPES
        );
    }

    default String encryptValue(String settingValue) {
        throw new EzyNotImplementedException(
            "encryptValue has not implemented yet"
        );
    }

    String decryptValue(String encryptedSettingValue);

    <T> T getObjectValue(String settingName, Class<T> objectType);

    default <T> T getObjectValue(
        String settingName,
        Class<T> objectType,
        T defaultValue
    ) {
        T answer = getObjectValue(
            settingName,
            objectType
        );
        return answer != null ? answer : defaultValue;
    }

    default <T> T getObjectValueOrDefault(
        String settingName,
        Class<T> objectType,
        Supplier<T> defaultValueSupplier
    ) {
        T answer = getObjectValue(
            settingName,
            objectType
        );
        return answer != null
            ? answer
            : defaultValueSupplier.get();
    }

    default String getPasswordValue(String settingName) {
        return getDecryptionValue(settingName);
    }

    default boolean getBooleanValue(String settingName) {
        return getBooleanValue(settingName, false);
    }

    default boolean getBooleanValue(
        String settingName,
        boolean defaultValue
    ) {
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

    default List<String> getListStringValue(String settingName) {
        return getListValue(settingName, String.class);
    }

    default List<String> getListStringValue(
        String settingName,
        List<String> defaultValue
    ) {
        List<String> value = getListValue(settingName, String.class);
        return value.isEmpty() ? defaultValue : value;
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

    default String getNodeName() {
        return getTextValue(SETTING_NODE_NAME);
    }

    default int getPaginationCountLimit() {
        return getIntValue(
            SETTING_NAME_PAGINATION_COUNT_LIMIT,
            LIMIT_1_000_000_RECORDS
        );
    }

    default String getWebSiteName() {
        String siteTitle = getTextValue(
            SETTING_NAME_WEB_SITE_TITLE
        );
        if (isNotBlank(siteTitle)) {
            return siteTitle;
        }
        String webUrl = getWebUrl();
        if (isNotBlank(webUrl)) {
            return Uris.getSiteName(webUrl);
        }
        String adminUrl = getAdminUrl();
        return Uris.getSiteName(adminUrl, "Website");
    }

    default String getAdminDateFormat() {
        return getTextValue(
            SETTING_NAME_ADMIN_DATE_FORMAT,
            DEFAULT_DATE_FORMAT
        );
    }

    default String getAdminTimeFormat() {
        return getTextValue(
            SETTING_NAME_ADMIN_TIME_FORMAT,
            DEFAULT_TIME_FORMAT
        );
    }

    default String getAdminDateTimeFormat() {
        return getTextValue(
            SETTING_NAME_ADMIN_DATETIME_FORMAT,
            DEFAULT_DATETIME_FORMAT
        );
    }

    default String getAdminDateMinuteFormat() {
        return getTextValue(
            SETTING_NAME_ADMIN_DATE_MINUTE_FORMAT,
            DEFAULT_DATE_MINUTE_FORMAT
        );
    }

    default String getWebDateFormat() {
        return getTextValue(
            SETTING_NAME_WEB_DATE_FORMAT,
            DEFAULT_DATE_FORMAT
        );
    }

    default String getWebTimeFormat() {
        return getTextValue(
            SETTING_NAME_WEB_TIME_FORMAT,
            DEFAULT_TIME_FORMAT
        );
    }

    default String getWebDateTimeFormat() {
        return getTextValue(
            SETTING_NAME_WEB_DATETIME_FORMAT,
            DEFAULT_DATETIME_FORMAT
        );
    }

    default String getWebDateMinuteFormat() {
        return getTextValue(
            SETTING_NAME_WEB_DATE_MINUTE_FORMAT,
            DEFAULT_DATE_MINUTE_FORMAT
        );
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
            DEFAULT_WEB_URL
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

    default boolean isUserAccessTokenHttpOnly() {
        return getBooleanValue(
            SETTING_NAME_USER_ACCESS_TOKEN_HTTP_ONLY
        );
    }

    default String getMediaUpDownloaderName() {
        return getTextValue(SETTING_NAME_MEDIA_UP_DOWN_LOADER_NAME);
    }

    default boolean isAllowSendStatisticsData() {
        return getBooleanValue(
            SETTING_NAME_ALLOW_SEND_STATISTICS_DATA,
            true
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

    default String randomWebEmail() {
        return randomEmailFromUrl(getWebUrl());
    }
}
