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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.function.EzyExceptionFunction;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.ezyfox.util.EzyLoggable;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;
import static java.nio.file.Files.readAllLines;
import static org.youngmonkeys.ezyplatform.manager.FileSystemManager.FILE_ENCRYPTION_KEYS;
import static org.youngmonkeys.ezyplatform.manager.FileSystemManager.FOLDER_SETTINGS;

public abstract class DefaultSettingService
    extends EzyLoggable
    implements SettingService {

    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;
    private final SettingRepository settingRepository;
    protected final File encryptionKeysFile;
    private final AtomicLong encryptionKeysFileLastModified
        = new AtomicLong();
    protected final AtomicReference<String> encryptionKey
        = new AtomicReference<>();
    private final Map<String, Object> cachedValues
        = new ConcurrentHashMap<>();
    private final Map<String, Long> lastUpdatedTimeBySettingName
        = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastChangedTimeBySettingName
        = new ConcurrentHashMap<>();
    private final Map<String, EzyExceptionFunction<String, Object>> converters
        = new ConcurrentHashMap<>();

    public static final LocalDateTime DEFAULT_LAST_CHANGED_TIME
        = LocalDateTime.of(1970, 1, 1, 0, 0);
    public static final int DEFAULT_CACHE_PERIOD_IN_SECOND = 5;
    public static final String DEFAULT_ENCRYPTION_KEY = "KSYzjcc8nqrBk8jXtW4QaMpr2suBU9vY";
    public static final String LAST_UPDATE_TIME_SUFFIX = "_last_updated_time";

    public DefaultSettingService(
        Scheduler scheduler,
        ObjectMapper objectMapper,
        FileSystemManager fileSystemManager,
        SettingRepository settingRepository
    ) {
        this.scheduler = scheduler;
        this.objectMapper = objectMapper;
        this.settingRepository = settingRepository;
        this.encryptionKeysFile = fileSystemManager.concatWithEzyHome(
            Paths.get(FOLDER_SETTINGS, FILE_ENCRYPTION_KEYS)
        );
        this.readAndSetEncryptionKeyFromFile();
        this.watchEncryptionKeysFile();
    }

    protected void watchEncryptionKeysFile() {
        this.scheduler.scheduleAtFixRate(() -> {
            if (encryptionKeysFile.exists()) {
                long lastModified = encryptionKeysFile.lastModified();
                if (lastModified != encryptionKeysFileLastModified.get()) {
                    readAndSetEncryptionKeyFromFile();
                }
            }
        }, 5000, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addValueConverter(
        String settingName,
        EzyExceptionFunction<String, Object> converter
    ) {
        this.converters.put(settingName, converter);
    }

    @Override
    public void scheduleCacheValue(
        String settingName,
        int periodInSecond
    ) {
        scheduler.scheduleAtFixRate(
            () -> cacheValueIfNotNull(
                settingName,
                fetchAndCacheLastChangedTimeAndParseValue(settingName)
            ),
            0L,
            periodInSecond,
            TimeUnit.SECONDS
        );
    }

    @Override
    public void watchLastUpdatedTime(
        String settingName,
        int periodInSecond,
        Runnable onLastUpdatedTimeChange
    ) {
        Runnable func = () -> fetchAndUpdateLastUpdatedTime(
            settingName,
            onLastUpdatedTimeChange
        );
        func.run();
        scheduler.scheduleAtFixRate(
            func,
            periodInSecond,
            periodInSecond,
            TimeUnit.SECONDS
        );
    }

    public void watchLastUpdatedTimeAndCache(
        String settingName,
        int periodInSecond
    ) {
        watchLastUpdatedTime(
            settingName,
            periodInSecond,
            () -> cacheValueIfNotNull(
                settingName,
                fetchAndCacheLastChangedTimeAndParseValue(settingName)
            )
        );
    }

    @Override
    public String decryptValue(String encryptedSettingValue) {
        return decryptValue(encryptedSettingValue, getEncryptionKey());
    }

    protected String decryptValue(
        String encryptedSettingValue,
        String encryptionKey
    ) {
        try {
            return new String(
                EzyAesCrypt.getDefault().decrypt(
                    EzyBase64.decode(encryptedSettingValue),
                    encryptionKey.getBytes()
                )
            );
        } catch (Exception e) {
            return encryptedSettingValue;
        }
    }

    @Override
    public <T> T getObjectValue(String settingName, Class<T> objectType) {
        String textValue = getTextValue(settingName);
        if (textValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(textValue, objectType);
        } catch (JsonProcessingException e) {
            logger.warn("can not read setting value to: {}", objectType, e);
            return null;
        }
    }

    @Override
    public String getDecryptionValue(String settingName) {
        String textValue = getTextValue(settingName);
        if (textValue == null) {
            return null;
        }
        return decryptValue(textValue);
    }

    @Override
    public Optional<String> getSettingValue(String settingName) {
        return settingRepository.findByFieldOptional(
                "name",
                settingName
            )
            .map(Setting::getValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCachedValue(String settingName, T defaultValue) {
        T value = (T) cachedValues.get(settingName);
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getAndParseValue(String settingName) {
        return getSettingValue(settingName)
            .map(it ->
                (T) convertValue(settingName, it)
            ).orElse(null);
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertValue(String settingName, String settingValue) {
        try {
            EzyExceptionFunction<String, Object> converter =
                converters.get(settingName);
            return (T) (converter == null ? settingValue : converter.apply(settingValue));
        } catch (Exception e) {
            logger.warn("convert setting: {} value: {} error", settingName, settingValue, e);
            return null;
        }
    }

    private <T> T fetchAndCacheLastChangedTimeAndParseValue(
        String settingName
    ) {
        LocalDateTime lastChangedTime = lastChangedTimeBySettingName.getOrDefault(
            settingName,
            DEFAULT_LAST_CHANGED_TIME
        );
        Setting setting = getChangedSetting(
            settingName,
            lastChangedTime
        );
        if (setting == null) {
            return null;
        }
        lastChangedTimeBySettingName.put(settingName, setting.getUpdatedAt());
        return convertValue(settingName, setting.getValue());
    }

    protected Setting getChangedSetting(
        String settingName,
        LocalDateTime lastChangedTime
    ) {
        return settingRepository
            .findByNameAndUpdatedAtGt(settingName, lastChangedTime);
    }

    public String getEncryptionKey() {
        return encryptionKey.get();
    }

    private void readAndSetEncryptionKeyFromFile() {
        String keyFromFile = readEncryptionKeyFile();
        if (isBlank(keyFromFile)) {
            if (encryptionKey.get() == null) {
                encryptionKey.set(DEFAULT_ENCRYPTION_KEY);
            }
        } else {
            encryptionKey.set(keyFromFile);
        }
    }

    private String readEncryptionKeyFile() {
        if (encryptionKeysFile.exists()) {
            try {
                List<String> lines = readAllLines(encryptionKeysFile.toPath());
                encryptionKeysFileLastModified.set(
                    encryptionKeysFile.lastModified()
                );
                return lines.size() > 0 ? lines.get(0) : null;
            } catch (IOException e) {
                logger.warn("read encryption key file error", e);
            }
        }
        return null;
    }

    private void cacheValueIfNotNull(String settingName, Object value) {
        if (value != null) {
            cachedValues.put(settingName, value);
        }
    }

    private void fetchAndUpdateLastUpdatedTime(
        String settingName,
        Runnable onLastUpdatedTimeChange
    ) {
        String key = settingName + LAST_UPDATE_TIME_SUFFIX;
        long settingValue = getLongValue(key);
        long currentValue = lastUpdatedTimeBySettingName.getOrDefault(
            settingName,
            0L
        );
        if ((currentValue == 0 && settingValue > 0)
            || (currentValue != 0 && settingValue > currentValue)
        ) {
            lastUpdatedTimeBySettingName.put(settingName, settingValue);
            processWithLogException(onLastUpdatedTimeChange::run, true);
        }
    }
}
