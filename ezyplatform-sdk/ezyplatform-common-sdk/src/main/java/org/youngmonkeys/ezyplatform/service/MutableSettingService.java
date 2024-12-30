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
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataType;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;

import java.util.Collection;

public abstract class MutableSettingService
    extends DefaultSettingService {

    private final SettingRepository settingRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    public MutableSettingService(
        Scheduler scheduler,
        ObjectMapper objectMapper,
        FileSystemManager fileSystemManager,
        SettingRepository settingRepository,
        DefaultModelToEntityConverter modelToEntityConverter
    ) {
        super(
            scheduler,
            objectMapper,
            fileSystemManager,
            settingRepository
        );
        this.settingRepository = settingRepository;
        this.modelToEntityConverter = modelToEntityConverter;
    }

    public void setLastUpdateTime(String settingName) {
        setLastUpdateTime(settingName, System.currentTimeMillis());
    }

    public void setLastUpdateTime(String settingName, long time) {
        setLongValue(
            settingName + LAST_UPDATE_TIME_SUFFIX,
            time
        );
    }

    public void setBooleanValue(String settingName, Boolean value) {
        saveSetting(
            settingName,
            DataType.BOOLEAN,
            value
        );
    }

    public void setIntValue(String settingName, Integer value) {
        saveSetting(
            settingName,
            DataType.INTEGER,
            value
        );
    }

    public void setLongValue(String settingName, Long value) {
        saveSetting(
            settingName,
            DataType.LONG,
            value
        );
    }

    public void setTextValue(String settingName, String value) {
        saveSetting(
            settingName,
            DataType.STRING,
            value
        );
    }

    public void setUrlValue(String settingName, String url) {
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

    @Override
    public void saveSetting(
        String name,
        DataType dataType,
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
}
