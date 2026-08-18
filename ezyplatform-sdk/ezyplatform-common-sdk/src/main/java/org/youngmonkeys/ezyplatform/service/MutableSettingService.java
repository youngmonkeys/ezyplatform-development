/*
 * Copyright 2024 youngmonkeys.org
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataType;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.io.PropertiesFileProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_DATA_KEYWORD_SPLIT_PATTERN;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_DATA_RANK_FUSION_CONSTANT;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_MAX_DATA_SEARCH_KEYWORDS;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_MIN_DATA_KEYWORD_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_NATURAL_LANGUAGE_STOP_WORDS;

public abstract class MutableSettingService
    extends DefaultSettingService {

    private final FileSystemManager fileSystemManager;
    private final SettingRepository settingRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    public MutableSettingService(
        Scheduler scheduler,
        ObjectMapper objectMapper,
        FileSystemManager fileSystemManager,
        SettingRepository settingRepository,
        DefaultEntityToModelConverter entityToModelConverter,
        DefaultModelToEntityConverter modelToEntityConverter
    ) {
        super(
            scheduler,
            objectMapper,
            fileSystemManager,
            settingRepository,
            entityToModelConverter
        );
        this.fileSystemManager = fileSystemManager;
        this.settingRepository = settingRepository;
        this.modelToEntityConverter = modelToEntityConverter;
    }

    public void setLastUpdateTime(
        String settingName
    ) {
        setLastUpdateTime(
            settingName,
            System.currentTimeMillis()
        );
    }

    public void setLastUpdateTime(
        String settingName,
        long time
    ) {
        setLongValue(
            settingName + LAST_UPDATE_TIME_SUFFIX,
            time
        );
    }

    public void setBooleanValue(
        String settingName,
        Boolean value
    ) {
        saveSetting(
            settingName,
            DataType.BOOLEAN,
            value
        );
    }

    public void setIntValue(
        String settingName,
        Integer value
    ) {
        saveSetting(
            settingName,
            DataType.INTEGER,
            value
        );
    }

    public void setLongValue(
        String settingName,
        Long value
    ) {
        saveSetting(
            settingName,
            DataType.LONG,
            value
        );
    }

    public void setTextValue(
        String settingName,
        String value
    ) {
        saveSetting(
            settingName,
            DataType.STRING,
            value
        );
    }

    public void setUrlValue(
        String settingName,
        String url
    ) {
        saveSetting(
            settingName,
            DataType.URL,
            url
        );
    }

    public void setArrayValue(
        String settingName,
        Collection<?> array
    ) {
        saveSetting(
            settingName,
            DataType.ARRAY,
            EzyStrings.join(array, ",")
        );
    }

    public void setObjectValue(
        String settingName,
        Object value
    ) {
        saveSetting(
            settingName,
            DataType.JSON,
            value
        );
    }

    public void setPasswordValue(
        String settingName,
        String value
    ) {
        saveSetting(
            settingName,
            DataType.PASSWORD,
            encryptValue(value)
        );
    }

    @Override
    public void saveSetting(
        String name,
        String dataType,
        Object value
    ) {
        Setting entity = settingRepository
            .findByField("name", name);
        if (entity == null) {
            entity = modelToEntityConverter.toSettingEntity(
                name,
                dataType,
                value
            );
        } else {
            modelToEntityConverter.mergeToSettingEntity(
                name,
                dataType,
                value,
                entity
            );
        }
        settingRepository.save(entity);
    }

    public void removeSetting(long settingId) {
        settingRepository.delete(settingId);
    }

    public void removeSetting(String settingName) {
        settingRepository.deleteByName(settingName);
    }

    @Override
    public String encryptValue(String settingValue) {
        return encryptValue(settingValue, getEncryptionKey());
    }

    protected String encryptValue(
        String settingValue,
        String encryptionKey
    ) {
        try {
            return EzyBase64.encode2utf(
                EzyAesCrypt.getDefault().encrypt(
                    settingValue.getBytes(),
                    encryptionKey.getBytes()
                )
            );
        } catch (Exception e) {
            return settingValue;
        }
    }

    public void saveLocalSettings(
        Map<Object, Object> settings
    ) {
        File file = fileSystemManager.concatWithEzyHomeToFile(
            LOCAL_SETTING_FILE_PATH
        );
        synchronized (localSettingRef) {
            if (file.exists()) {
                loadLocalSettingsIfNeed(file);
            }
            Map<Object, Object> current = localSettingRef.get();
            Map<Object, Object> mergedSettings = current != null
                ? new HashMap<>(current)
                : new HashMap<>();
            for (Map.Entry<Object, Object> entry : settings.entrySet()) {
                if (entry.getValue() == null) {
                    mergedSettings.remove(entry.getKey());
                } else {
                    mergedSettings.put(entry.getKey(), entry.getValue());
                }
            }
            writeLocalSettings(file, mergedSettings);
        }
    }

    public void saveLocalSetting(
        String settingName,
        Object value
    ) {
        File file = fileSystemManager.concatWithEzyHomeToFile(
            LOCAL_SETTING_FILE_PATH
        );
        synchronized (localSettingRef) {
            if (file.exists()) {
                loadLocalSettingsIfNeed(file);
            }
            Map<Object, Object> current = localSettingRef.get();
            Map<Object, Object> settings = current != null
                ? new HashMap<>(current)
                : new HashMap<>();
            if (value == null) {
                settings.remove(settingName);
            } else {
                settings.put(settingName, value);
            }
            writeLocalSettings(file, settings);
        }
    }

    private void writeLocalSettings(
        File file,
        Map<Object, Object> settings
    ) {
        PropertiesFileProxy.sortAndWrite(settings, file);
        localSettingRef.set(settings);
        localSettingLastModified.set(file.lastModified());
    }

    public void setNaturalLanguageStopWords(
        Collection<String> value
    ) {
        setArrayValue(
            SETTING_NAME_NATURAL_LANGUAGE_STOP_WORDS,
            value
        );
    }

    public void setDataKeywordSplitPattern(
        String value
    ) {
        setTextValue(
            SETTING_NAME_DATA_KEYWORD_SPLIT_PATTERN,
            value
        );
    }

    public void setMaxDataSearchKeywords(
        int value
    ) {
        setIntValue(
            SETTING_NAME_MAX_DATA_SEARCH_KEYWORDS,
            value
        );
    }

    public void setMinDataKeywordLength(
        int value
    ) {
        setIntValue(
            SETTING_NAME_MIN_DATA_KEYWORD_LENGTH,
            value
        );
    }

    public void setDataRankFusionConstant(
        int value
    ) {
        setIntValue(
            SETTING_NAME_DATA_RANK_FUSION_CONSTANT,
            value
        );
    }
}
