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

package org.youngmonkeys.ezyplatform.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.constant.CommonConstants;
import org.youngmonkeys.ezyplatform.entity.AdminActivityHistory;
import org.youngmonkeys.ezyplatform.entity.ContentTemplate;
import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.entity.DataIndex;
import org.youngmonkeys.ezyplatform.entity.DataMapping;
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.entity.DataType;
import org.youngmonkeys.ezyplatform.entity.Letter;
import org.youngmonkeys.ezyplatform.entity.LetterReceiver;
import org.youngmonkeys.ezyplatform.entity.Link;
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.entity.MediaStatus;
import org.youngmonkeys.ezyplatform.entity.Notification;
import org.youngmonkeys.ezyplatform.entity.NotificationReceiver;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.entity.UniqueData;
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.entity.UserKeyword;
import org.youngmonkeys.ezyplatform.entity.UserRole;
import org.youngmonkeys.ezyplatform.model.AddAdminActivityHistoryModel;
import org.youngmonkeys.ezyplatform.model.AddLetterModel;
import org.youngmonkeys.ezyplatform.model.AddLetterReceiverModel;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.AddNotificationModel;
import org.youngmonkeys.ezyplatform.model.AddNotificationReceiverModel;
import org.youngmonkeys.ezyplatform.model.AddUserKeywordModel;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.model.ReplaceMediaModel;
import org.youngmonkeys.ezyplatform.model.SaveContentTemplateModel;
import org.youngmonkeys.ezyplatform.model.SaveDataKeywordModel;
import org.youngmonkeys.ezyplatform.model.SaveDataMappingModel;
import org.youngmonkeys.ezyplatform.model.SaveLinkModel;
import org.youngmonkeys.ezyplatform.model.UniqueDataKeyValueModel;
import org.youngmonkeys.ezyplatform.model.UniqueDataModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_ACTIVITY_HISTORY_PARAMETERS_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;
import static org.youngmonkeys.ezyplatform.util.Strings.toBigDecimalOrZero;
import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

@AllArgsConstructor
@SuppressWarnings("MethodCount")
public class DefaultModelToEntityConverter {

    protected final ClockProxy clock;
    protected final ObjectMapper objectMapper;

    public Media toEntity(
        AddMediaModel model,
        String uploadFrom
    ) {
        Media entity = new Media();
        entity.setUploadFrom(uploadFrom);
        entity.setOwnerAdminId(model.getOwnerAdminId());
        entity.setOwnerUserId(model.getOwnerUserId());
        entity.setName(model.getFileName());
        entity.setUrl(model.getUrl());
        entity.setOriginalName(model.getOriginalFileName());
        entity.setType(model.getMediaType());
        entity.setMimeType(model.getMimeType());
        entity.setAlternativeText(EMPTY_STRING);
        entity.setCaption(EMPTY_STRING);
        entity.setDescription(EMPTY_STRING);
        entity.setFileSize(model.getFileSize());
        entity.setPublicMedia(!model.isNotPublic());
        entity.setStatus(MediaStatus.ADDED.toString());
        LocalDateTime now = clock.nowDateTime();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public Letter toEntity(AddLetterModel model) {
        Letter letter = new Letter();
        letter.setTitle(model.getTitle());
        letter.setContent(model.getContent());
        letter.setContentType(model.getContentType());
        letter.setType(model.getType());
        letter.setFromAdminId(model.getFromAdminId());
        letter.setFromUserId(model.getFromUserId());
        letter.setParentId(model.getParentId());
        letter.setStatus(model.getLetterStatus());
        letter.setCreatedAt(clock.nowDateTime());
        return letter;
    }

    public Notification toEntity(AddNotificationModel model) {
        Notification notification = new Notification();
        notification.setTitle(model.getTitle());
        notification.setContent(model.getContent());
        notification.setContentType(model.getContentType());
        notification.setIconImage(model.getIconImage());
        notification.setType(model.getType());
        notification.setFromAdminId(model.getFromAdminId());
        notification.setFromUserId(model.getFromUserId());
        notification.setDeepLink(model.getDeepLink());
        notification.setStatus(model.getNotificationStatus());
        notification.setCreatedAt(clock.nowDateTime());
        return notification;
    }

    public LetterReceiver toEntity(AddLetterReceiverModel model) {
        LetterReceiver entity = new LetterReceiver();
        entity.setLetterId(model.getLetterId());
        entity.setToUserId(model.getToUserId());
        entity.setToAdminId(model.getToAdminId());
        entity.setStatus(model.getStatus());
        entity.setConfidenceLevel(model.getConfidenceLevel());
        entity.setImportantLevel(model.getImportantLevel());
        entity.setNotificationReceiverId(model.getNotificationReceiverId());
        entity.setSentAt(clock.nowDateTime());
        return entity;
    }

    public LetterReceiver toEntity(
        long letterId,
        AddLetterModel model,
        LocalDateTime now
    ) {
        LetterReceiver entity = new LetterReceiver();
        entity.setLetterId(letterId);
        entity.setStatus(model.getSendStatus());
        entity.setConfidenceLevel(model.getConfidenceLevel());
        entity.setImportantLevel(model.getImportantLevel());
        entity.setNotificationReceiverId(model.getNotificationReceiverId());
        entity.setSentAt(now);
        return entity;
    }

    public NotificationReceiver toEntity(
        AddNotificationReceiverModel model
    ) {
        NotificationReceiver entity = new NotificationReceiver();
        entity.setNotificationId(model.getNotificationId());
        entity.setToUserId(model.getToUserId());
        entity.setToAdminId(model.getToAdminId());
        entity.setStatus(model.getStatus());
        entity.setConfidenceLevel(model.getConfidenceLevel());
        entity.setImportantLevel(model.getImportantLevel());
        entity.setSentAt(clock.nowDateTime());
        return entity;
    }

    public NotificationReceiver toEntity(
        long notificationId,
        AddNotificationModel model,
        LocalDateTime now
    ) {
        NotificationReceiver entity = new NotificationReceiver();
        entity.setNotificationId(notificationId);
        entity.setStatus(model.getSendStatus());
        entity.setConfidenceLevel(model.getConfidenceLevel());
        entity.setImportantLevel(model.getImportantLevel());
        entity.setSentAt(now);
        return entity;
    }

    public UserKeyword toEntity(AddUserKeywordModel model) {
        UserKeyword entity = new UserKeyword();
        entity.setUserId(model.getUserId());
        entity.setKeyword(model.getKeyword());
        mergeToEntity(model, entity);
        entity.setCreatedAt(entity.getUpdatedAt());
        return entity;
    }

    public AdminActivityHistory toEntity(AddAdminActivityHistoryModel model) {
        String parameters = valueToJsonOrNull(model.getParameters());
        if (parameters != null
            && parameters.length() > MAX_ACTIVITY_HISTORY_PARAMETERS_LENGTH
        ) {
            parameters = parameters.substring(
                ZERO,
                MAX_ACTIVITY_HISTORY_PARAMETERS_LENGTH
            );
        }
        AdminActivityHistory entity = new AdminActivityHistory();
        entity.setAdminId(model.getAdminId());
        entity.setFeature(model.getFeature());
        entity.setMethod(model.getMethod());
        entity.setUri(model.getUri());
        entity.setUriType(model.getUriType());
        entity.setParameters(parameters);
        entity.setCreatedAt(clock.nowDateTime());
        return entity;
    }

    public Link toEntity(SaveLinkModel model) {
        Link entity = new Link();
        mergeToEntity(model, entity);
        entity.setCreatedAt(entity.getUpdatedAt());
        return entity;
    }

    public DataIndex toEntity(
        String dataType,
        SaveDataKeywordModel model
    ) {
        DataIndex entity = new DataIndex();
        entity.setDataType(dataType);
        entity.setDataId(model.getDataId());
        entity.setKeyword(model.getKeyword());
        mergeToEntity(model, entity);
        entity.setCreatedAt(entity.getUpdatedAt());
        return entity;
    }

    public DataI18n toEntity(DataI18nModel model) {
        DataI18n entity = new DataI18n();
        entity.setDataType(model.getDataType());
        entity.setDataId(model.getDataId());
        entity.setLanguage(model.getLanguage());
        entity.setFieldName(model.getFieldName());
        entity.setFieldValue(model.getFieldValue());
        return entity;
    }

    public DataMapping toEntity(SaveDataMappingModel model) {
        DataMapping entity = new DataMapping();
        entity.setMappingName(model.getMappingName());
        entity.setFromDataId(model.getFromDataId());
        entity.setToDataId(model.getToDataId());
        entity.setDisplayOrder(model.getDisplayOrder());
        BigInteger quantity = model.getQuantity();
        if (quantity == null) {
            quantity = BigInteger.ZERO;
        }
        entity.setQuantity(quantity);
        BigInteger remainingQuantity = model.getRemainingQuantity();
        if (remainingQuantity == null) {
            remainingQuantity = BigInteger.ZERO;
        }
        entity.setRemainingQuantity(remainingQuantity);
        BigInteger numberData = model.getNumberData();
        if (numberData == null) {
            numberData = BigInteger.ZERO;
        }
        entity.setNumberData(numberData);
        BigDecimal decimalData = model.getDecimalData();
        if (decimalData == null) {
            decimalData = BigDecimal.ZERO;
        }
        entity.setDecimalData(decimalData);
        entity.setTextData(model.getTextData());
        entity.setMetadata(model.getMetadata());
        entity.setMappedAt(clock.nowDateTime());
        return entity;
    }

    public UniqueData toEntity(UniqueDataModel model) {
        UniqueData entity = new UniqueData();
        entity.setDataType(model.getDataType());
        entity.setDataId(model.getDataId());
        entity.setUniqueKey(model.getUniqueKey());
        String textValue = model.getTextValue();
        entity.setTextValue(textValue);
        BigInteger numberValue = model.getNumberValue();
        if (numberValue == null) {
            numberValue = toBigIntegerOrZero(textValue);
        }
        entity.setNumberValue(numberValue);
        BigDecimal decimalValue = model.getDecimalValue();
        if (decimalValue == null) {
            decimalValue = toBigDecimalOrZero(textValue);
        }
        entity.setDecimalValue(decimalValue);
        entity.setMetadata(model.getMetadata());
        return entity;
    }

    public UniqueData toEntity(
        String dataType,
        long dataId,
        UniqueDataKeyValueModel model
    ) {
        UniqueData entity = new UniqueData();
        entity.setDataType(dataType);
        entity.setDataId(dataId);
        entity.setUniqueKey(model.getUniqueKey());
        String textValue = model.getTextValue();
        entity.setTextValue(textValue);
        BigInteger numberValue = model.getNumberValue();
        if (numberValue == null) {
            numberValue = toBigIntegerOrZero(textValue);
        }
        entity.setNumberValue(numberValue);
        BigDecimal decimalValue = model.getDecimalValue();
        if (decimalValue == null) {
            decimalValue = toBigDecimalOrZero(textValue);
        }
        entity.setDecimalValue(decimalValue);
        entity.setMetadata(model.getMetadata());
        return entity;
    }

    public ContentTemplate toEntity(
        String creatorType,
        long creatorId,
        SaveContentTemplateModel model
    ) {
        ContentTemplate entity = new ContentTemplate();
        entity.setCreatorType(creatorType);
        entity.setCreatorId(creatorId);
        mergeToEntity(model, entity);
        entity.setCreatedAt(entity.getUpdatedAt());
        return entity;
    }

    public UserAccessToken toUserAccessTokenEntity(
        long userId,
        String token,
        String tokenType,
        long tokenExpiredTimeInDay,
        String status
    ) {
        LocalDateTime now = clock.nowDateTime();
        LocalDateTime expiredAt = now.plusDays(tokenExpiredTimeInDay);
        return toUserAccessTokenEntity(
            userId,
            token,
            tokenType,
            status,
            now,
            expiredAt
        );
    }

    public UserAccessToken toUserAccessTokenEntity(
        long userId,
        String token,
        String tokenType,
        long tokenExpiredTime,
        TimeUnit tokenExpiredTimeUnit,
        String status
    ) {
        LocalDateTime now = clock.nowDateTime();
        LocalDateTime expiredAt = now.plusSeconds(
            tokenExpiredTimeUnit.toSeconds(tokenExpiredTime)
        );
        return toUserAccessTokenEntity(
            userId,
            token,
            tokenType,
            status,
            now,
            expiredAt
        );
    }

    public UserAccessToken toUserAccessTokenEntity(
        long userId,
        String token,
        String tokenType,
        String status,
        LocalDateTime now,
        LocalDateTime expiredAt
    ) {
        UserAccessToken userAccessToken = new UserAccessToken();
        userAccessToken.setId(token);
        userAccessToken.setUserId(userId);
        userAccessToken.setTokenType(tokenType);
        userAccessToken.setStatus(status);
        userAccessToken.setCreatedAt(now);
        userAccessToken.setExpiredAt(expiredAt);
        return userAccessToken;
    }

    public UserRole toUserRoleByUserIdAndRoleId(
        long userId,
        long roleId
    ) {
        UserRole entity = new UserRole();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        entity.setCreatedAt(clock.nowDateTime());
        return entity;
    }

    public Setting toSettingEntity(
        String name,
        String dataType,
        Object value
    ) {
        Setting entity = new Setting();
        mergeToSettingEntity(name, dataType, value, entity);
        entity.setCreatedAt(entity.getUpdatedAt());
        return entity;
    }

    public DataRecordCount toNewDataRecordCount(
        String dataType
    ) {
        DataRecordCount entity = new DataRecordCount();
        entity.setDataType(dataType);
        entity.setLastCountedAt(clock.nowDateTime());
        return entity;
    }

    public List<NotificationReceiver> toEntities(
        long notificationId,
        AddNotificationModel model
    ) {
        LocalDateTime now = clock.nowDateTime();
        List<NotificationReceiver> entities = new ArrayList<>();
        for (long toAdminId : model.getToAdminIds()) {
            if (toAdminId <= ZERO_LONG) {
                continue;
            }
            NotificationReceiver entity = toEntity(
                notificationId,
                model,
                now
            );
            entity.setToAdminId(toAdminId);
            entities.add(entity);
        }
        for (long toUserId : model.getToUserIds()) {
            if (toUserId <= ZERO_LONG) {
                continue;
            }
            NotificationReceiver entity = toEntity(
                notificationId,
                model,
                now
            );
            entity.setToUserId(toUserId);
            entities.add(entity);
        }
        return entities;
    }

    public List<LetterReceiver> toEntities(
        long letterId,
        AddLetterModel model
    ) {
        LocalDateTime now = clock.nowDateTime();
        List<LetterReceiver> entities = new ArrayList<>();
        for (long toAdminId : model.getToAdminIds()) {
            if (toAdminId <= ZERO_LONG) {
                continue;
            }
            LetterReceiver entity = toEntity(
                letterId,
                model,
                now
            );
            entity.setToAdminId(toAdminId);
            entities.add(entity);
        }
        for (long toUserId : model.getToUserIds()) {
            if (toUserId <= ZERO_LONG) {
                continue;
            }
            LetterReceiver entity = toEntity(
                letterId,
                model,
                now
            );
            entity.setToUserId(toUserId);
            entities.add(entity);
        }
        return entities;
    }

    public List<DataMapping> toEntities(
        String mappingName,
        Collection<Long> fromDataIds,
        Long toDataId
    ) {
        return newArrayList(
            fromDataIds,
            fromDataId -> toEntity(
                SaveDataMappingModel.builder()
                    .mappingName(mappingName)
                    .fromDataId(fromDataId)
                    .toDataId(toDataId)
                    .build()
            )
        );
    }

    public List<DataMapping> toEntities(
        String mappingName,
        Long fromDataId,
        Collection<Long> toDataIds
    ) {
        return newArrayList(
            toDataIds,
            toDataId -> toEntity(
                SaveDataMappingModel.builder()
                    .mappingName(mappingName)
                    .fromDataId(fromDataId)
                    .toDataId(toDataId)
                    .build()
            )
        );
    }

    public List<DataMapping> toEntities(
        String mappingName,
        Map<Long, Long> toDataIdByFromDataId
    ) {
        return newArrayList(
            toDataIdByFromDataId.entrySet(),
            e -> toEntity(
                SaveDataMappingModel.builder()
                    .mappingName(mappingName)
                    .fromDataId(e.getKey())
                    .toDataId(e.getValue())
                    .build()
            )
        );
    }

    public void mergeToEntity(
        UpdateMediaModel model,
        Media entity
    ) {
        if (model.isUpdateType()) {
            String type = model.getType();
            if (isNotBlank(type)) {
                entity.setType(type);
            }
        }
        entity.setAlternativeText(model.getAlternativeText());
        entity.setTitle(model.getTitle());
        entity.setCaption(model.getCaption());
        entity.setDescription(model.getDescription());
        entity.setFileSize(model.getFileSize());
        entity.setPublicMedia(!model.isNotPublic());
        if (model.isUpdateUrl()) {
            entity.setUrl(model.getUrl());
        }
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(
        ReplaceMediaModel model,
        Media entity
    ) {
        entity.setName(model.getFileName());
        entity.setOriginalName(model.getOriginalFileName());
        entity.setType(model.getMediaType());
        entity.setMimeType(model.getMimeType());
        entity.setFileSize(model.getFileSize());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(
        SaveLinkModel model,
        Link entity
    ) {
        String sourceType = model.getSourceType();
        if (isBlank(sourceType)) {
            sourceType = CommonConstants.UNKNOWN;
        }
        entity.setLinkUri(model.getLinkUri());
        entity.setLinkType(model.getLinkType());
        entity.setImageId(model.getLinkImageId());
        entity.setDescription(model.getDescription());
        entity.setSourceType(sourceType);
        entity.setSourceId(model.getSourceId());
        entity.setStatus(model.getStatus());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(
        SaveDataKeywordModel model,
        DataIndex entity
    ) {
        entity.setPriority(model.getPriority());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(
        AddUserKeywordModel model,
        UserKeyword entity
    ) {
        entity.setPriority(model.getPriority());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(
        SaveContentTemplateModel model,
        ContentTemplate entity
    ) {
        String templateName = model.getTemplateName();
        if (isBlank(templateName)) {
            templateName = UUID.randomUUID().toString();
        }
        entity.setOwnerType(model.getOwnerType());
        entity.setOwnerId(model.getOwnerId());
        entity.setTemplateType(model.getTemplateType());
        entity.setTemplateName(templateName);
        entity.setTitleTemplate(model.getTitleTemplate());
        entity.setContentTemplate(model.getContentTemplate());
        String contentType = model.getContentType();
        if (contentType != null) {
            entity.setContentType(contentType);
        }
        entity.setStatus(model.getStatus());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeUpdatedAtToEntity(
        Media entity
    ) {
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToSettingEntity(
        String name,
        String dataType,
        Object value,
        Setting entity
    ) {
        String valueString = EMPTY_STRING;
        if (value != null) {
            if (DataType.JSON.equalsValue(dataType)) {
                valueString = valueToJson(value);
            } else {
                valueString = String.valueOf(value);
            }
        }
        entity.setName(name);
        entity.setDataType(dataType);
        entity.setValue(valueString);
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public String valueToJsonOrEmpty(Object value) {
        String answer = valueToJsonOrNull(value);
        return answer != null ? answer : EMPTY_STRING;
    }

    public String valueToJsonOrNull(Object value) {
        return valueToJson(value, Boolean.FALSE);
    }

    public String valueToJson(Object value) {
        return valueToJson(value, Boolean.TRUE);
    }

    public String valueToJson(
        Object value,
        boolean throwException
    ) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value);
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            if (throwException) {
                throw new IllegalArgumentException(
                    "can not convert value to json",
                    e
                );
            }
            return null;
        }
    }
}
