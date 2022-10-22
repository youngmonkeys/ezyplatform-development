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
import org.youngmonkeys.ezyplatform.entity.*;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@AllArgsConstructor
public class DefaultModelToEntityConverter {

    protected final ClockProxy clock;
    protected final ObjectMapper objectMapper; 
    
    public Media toEntity(AddMediaModel model) {
        return toEntity(model, UploadFrom.ADMIN);
    }

    public Media toEntity(AddMediaModel model, UploadFrom uploadFrom) {
        LocalDateTime now = clock.nowDateTime();
        Media entity = new Media();
        if (uploadFrom == UploadFrom.ADMIN) {
            entity.setOwnerAdminId(model.getOwnerId());
        } else {
            entity.setOwnerUserId(model.getOwnerId());
        }
        entity.setUploadFrom(uploadFrom);
        entity.setName(model.getFileName());
        entity.setUrl(model.getUrl());
        entity.setOriginalName(model.getOriginalFileName());
        entity.setType(model.getMediaType());
        entity.setMimeType(model.getMimeType());
        entity.setAlternativeText(EMPTY_STRING);
        entity.setCaption(EMPTY_STRING);
        entity.setDescription(EMPTY_STRING);
        entity.setPublicMedia(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public Letter toEntity(AddLetterModel model) {
        Letter letter = new Letter();
        letter.setTitle(model.getTitle());
        letter.setContent(model.getContent());
        letter.setType(model.getType());
        letter.setFromAdminId(model.getFromAdminId());
        letter.setFromUserId(model.getFromUserId());
        letter.setStatus(model.getLetterStatus());
        letter.setCreatedAt(clock.nowDateTime());
        return letter;
    }

    public Notification toEntity(AddNotificationModel model) {
        Notification notification = new Notification();
        notification.setTitle(model.getTitle());
        notification.setContent(model.getContent());
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
        entity.setPriority(model.getPriority());
        entity.setCreatedAt(clock.nowDateTime());
        return entity;
    }

    public AdminActivityHistory toEntity(AddAdminActivityHistoryModel model) {
        AdminActivityHistory entity = new AdminActivityHistory();
        entity.setAdminId(model.getAdminId());
        entity.setFeature(model.getFeature());
        entity.setMethod(model.getMethod());
        entity.setUri(model.getUri());
        entity.setUriType(model.getUriType());
        entity.setParameters(valueToJsonOrNull(model.getParameters()));
        entity.setCreatedAt(clock.nowDateTime());
        return entity;
    }

    public Link toEntity(SaveLinkModel model) {
        Link entity = new Link();
        entity.setLinkUri(model.getLinkUri());
        entity.setLinkType(model.getLinkType());
        entity.setImageId(model.getLinkImageId());
        entity.setDescription(model.getDescription());
        LocalDateTime now = clock.nowDateTime();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public DataIndex toEntity(AddDataKeywordModel model) {
        DataIndex entity = new DataIndex();
        entity.setDataType(model.getDataType());
        entity.setDataId(model.getDataId());
        entity.setKeyword(model.getKeyword());
        entity.setPriority(model.getPriority());
        entity.setCreatedAt(clock.nowDateTime());
        return entity;
    }

    public List<NotificationReceiver> toEntities(
        long notificationId,
        AddNotificationModel model
    ) {
        LocalDateTime now = clock.nowDateTime();
        List<NotificationReceiver> entities = new ArrayList<>();
        for (long toAdminId : model.getToAdminIds()) {
            if (toAdminId <= 0) {
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
            if (toUserId <= 0) {
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
            if (toAdminId <= 0) {
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
            if (toUserId <= 0) {
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

    public void mergeToEntity(
        UpdateMediaModel model,
        Media entity
    ) {
        entity.setAlternativeText(model.getAlternativeText());
        entity.setTitle(model.getTitle());
        entity.setCaption(model.getCaption());
        entity.setDescription(model.getDescription());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    public void mergeToEntity(SaveLinkModel model, Link entity) {
        entity.setLinkUri(model.getLinkUri());
        entity.setLinkType(model.getLinkType());
        entity.setImageId(model.getLinkImageId());
        entity.setDescription(model.getDescription());
        entity.setUpdatedAt(clock.nowDateTime());
    }

    protected String valueToJsonOrEmpty(Object value) {
        String answer = valueToJsonOrNull(value);
        return answer != null ? answer : EMPTY_STRING;
    }

    protected String valueToJsonOrNull(Object value) {
        return valueToJson(value, false);
    }

    protected String valueToJson(Object value) {
        return valueToJson(value, true);
    }

    private String valueToJson(Object value, boolean throwException) {
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
                throw new IllegalArgumentException("can not convert value to json", e);
            }
            return null;
        }
    }
}
