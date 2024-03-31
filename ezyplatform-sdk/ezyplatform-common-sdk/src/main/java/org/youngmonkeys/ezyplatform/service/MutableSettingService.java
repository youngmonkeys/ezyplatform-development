package org.youngmonkeys.ezyplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataType;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;

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
