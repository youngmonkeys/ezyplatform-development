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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.*;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.util.LocalDateTimes;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@AllArgsConstructor
public abstract class DefaultEntityToModelConverter {

    protected final ZoneId zoneId;

    public AdminModel toModel(Admin entity) {
        if (entity == null) {
            return null;
        }
        return AdminModel.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .displayName(entity.getDisplayName())
            .url(entity.getUrl())
            .avatarImageId(entity.getAvatarImageId())
            .coverImageId(entity.getCoverImageId())
            .status(entity.getStatus())
            .password(entity.getPassword())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public MediaModel toModel(Media entity) {
        return MediaModel.builder()
            .id(entity.getId())
            .name(entity.getName())
            .url(entity.getUrl())
            .originalName(entity.getOriginalName())
            .type(entity.getType())
            .mimeType(entity.getMimeType())
            .uploadFrom(entity.getUploadFrom())
            .ownerUserId(entity.getOwnerUserId())
            .ownerAdminId(entity.getOwnerAdminId())
            .title(entity.getTitle())
            .caption(entity.getCaption())
            .alternativeText(entity.getAlternativeText())
            .description(entity.getDescription())
            .publicMedia(entity.isPublicMedia())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public UserModel toModel(User entity) {
        return toModel(entity, true, true);
    }

    public UserModel toModel(
        User entity,
        boolean includePassword,
        boolean includeActivationKey
    ) {
        if (entity == null) {
            return null;
        }
        return UserModel.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .password(includePassword ? entity.getPassword() : EMPTY_STRING)
            .phone(entity.getPhone())
            .url(entity.getUrl())
            .displayName(entity.getDisplayName())
            .avatarImageId(entity.getAvatarImageId())
            .coverImageId(entity.getCoverImageId())
            .activationKey(includeActivationKey ? entity.getActivationKey() : EMPTY_STRING)
            .status(UserStatus.valueOf(entity.getStatus()))
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public UserRoleNameModel toModel(UserRoleName entity) {
        if (entity == null) {
            return null;
        }
        return UserRoleNameModel.builder()
            .id(entity.getId())
            .name(entity.getName())
            .displayName(entity.getDisplayName())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .build();
    }

    public NotificationModel toModel(
        Notification notification,
        NotificationReceiver notificationReceiver,
        boolean includeFromAdmin
    ) {
        return NotificationModel.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .content(notification.getContent())
            .iconImage(notification.getIconImage())
            .deepLink(notification.getDeepLink())
            .status(notification.getStatus())
            .fromAdminId(includeFromAdmin ? notification.getFromAdminId() : 0L)
            .fromUserId(notification.getFromUserId())
            .toAdminId(notificationReceiver.getToAdminId())
            .toUserId(notificationReceiver.getToUserId())
            .confidenceLevel(notificationReceiver.getConfidenceLevel())
            .importantLevel(notificationReceiver.getImportantLevel())
            .receiveStatus(notificationReceiver.getStatus())
            .sentAt(toTimestamp(notificationReceiver.getSentAt()))
            .receivedAt(toTimestamp(notificationReceiver.getReceivedAt()))
            .readAt(toTimestamp(notificationReceiver.getReadAt()))
            .build();
    }

    public LetterModel toModel(
        Letter letter,
        LetterReceiver letterReceiver,
        boolean includeFromAdmin
    ) {
        return LetterModel.builder()
            .id(letter.getId())
            .title(letter.getTitle())
            .content(letter.getContent())
            .status(letter.getStatus())
            .fromAdminId(includeFromAdmin ? letter.getFromAdminId() : 0L)
            .fromUserId(letter.getFromUserId())
            .toAdminId(letterReceiver.getToAdminId())
            .toUserId(letterReceiver.getToUserId())
            .confidenceLevel(letterReceiver.getConfidenceLevel())
            .importantLevel(letterReceiver.getImportantLevel())
            .receiveStatus(letterReceiver.getStatus())
            .sentAt(toTimestamp(letterReceiver.getSentAt()))
            .receivedAt(toTimestamp(letterReceiver.getReceivedAt()))
            .readAt(toTimestamp(letterReceiver.getReadAt()))
            .build();
    }

    public ContentTemplateModel toModel(ContentTemplate entity) {
        if (entity == null) {
            return null;
        }
        return ContentTemplateModel.builder()
            .id(entity.getId())
            .type(entity.getTemplateType())
            .name(entity.getTemplateName())
            .titleTemplate(entity.getTitleTemplate())
            .contentTemplate(entity.getContentTemplate())
            .creatorId(entity.getCreatorId())
            .status(entity.getStatus())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public long toTimestamp(LocalDateTime localDateTime) {
        return LocalDateTimes.toTimestamp(localDateTime, zoneId);
    }
}
