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
import static org.youngmonkeys.ezyplatform.model.MediaNameModel.getMediaUrlOrNull;

@AllArgsConstructor
public class DefaultEntityToModelConverter {

    protected final ZoneId zoneId;

    public AdminModel toModel(Admin entity) {
        if (entity == null) {
            return null;
        }
        return AdminModel.builder()
            .id(entity.getId())
            .uuid(entity.getUuid())
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

    public AdminAccessTokenModel toModel(AdminAccessToken entity) {
        if (entity == null) {
            return null;
        }
        return AdminAccessTokenModel.builder()
            .adminId(entity.getAdminId())
            .accessToken(entity.getId())
            .renewalCount(entity.getRenewalCount())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .expiredAt(entity.getExpiredAt())
            .createdAtTimestamp(toTimestamp(entity.getCreatedAt()))
            .expiredAtTimestamp(toTimestamp(entity.getExpiredAt()))
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
            .uuid(entity.getUuid())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .password(includePassword ? entity.getPassword() : EMPTY_STRING)
            .phone(entity.getPhone())
            .url(entity.getUrl())
            .displayName(entity.getDisplayName())
            .avatarImageId(entity.getAvatarImageId())
            .coverImageId(entity.getCoverImageId())
            .activationKey(includeActivationKey ? entity.getActivationKey() : EMPTY_STRING)
            .status(entity.getStatus())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public UserAccessTokenModel toModel(UserAccessToken entity) {
        if (entity == null) {
            return null;
        }
        return UserAccessTokenModel.builder()
            .userId(entity.getUserId())
            .accessToken(entity.getId())
            .renewalCount(entity.getRenewalCount())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .expiredAt(entity.getExpiredAt())
            .createdAtTimestamp(toTimestamp(entity.getCreatedAt()))
            .expiredAtTimestamp(toTimestamp(entity.getExpiredAt()))
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
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public NotificationModel toModel(
        Notification notification,
        NotificationReceiver entity,
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
            .toAdminId(entity.getToAdminId())
            .toUserId(entity.getToUserId())
            .notificationReceiverId(entity.getId())
            .confidenceLevel(entity.getConfidenceLevel())
            .importantLevel(entity.getImportantLevel())
            .receiveStatus(entity.getStatus())
            .sentAt(toTimestamp(entity.getSentAt()))
            .receivedAt(toTimestamp(entity.getReceivedAt()))
            .readAt(toTimestamp(entity.getReadAt()))
            .build();
    }

    public LetterModel toModel(
        Letter letter,
        LetterReceiver entity
    ) {
        return LetterModel.builder()
            .id(letter.getId())
            .title(letter.getTitle())
            .content(letter.getContent())
            .status(letter.getStatus())
            .fromAdminId(letter.getFromAdminId())
            .fromUserId(letter.getFromUserId())
            .toAdminId(entity.getToAdminId())
            .toUserId(entity.getToUserId())
            .parentId(letter.getParentId())
            .letterReceiverId(entity.getId())
            .notificationReceiverId(entity.getNotificationReceiverId())
            .confidenceLevel(entity.getConfidenceLevel())
            .importantLevel(entity.getImportantLevel())
            .receiveStatus(entity.getStatus())
            .sentAt(toTimestamp(entity.getSentAt()))
            .receivedAt(toTimestamp(entity.getReceivedAt()))
            .readAt(toTimestamp(entity.getReadAt()))
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

    public AdminRoleModel toModel(AdminRole entity) {
        if (entity == null) {
            return null;
        }
        return AdminRoleModel.builder()
            .adminId(entity.getAdminId())
            .roleId(entity.getRoleId())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .build();
    }

    public UserRoleModel toModel(UserRole entity) {
        if (entity == null) {
            return null;
        }
        return UserRoleModel.builder()
            .userId(entity.getUserId())
            .roleId(entity.getRoleId())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .build();
    }

    public LinkModel toModel(Link entity, MediaNameModel image) {
        return toModel(entity, getMediaUrlOrNull(image));
    }

    public LinkModel toModel(Link entity, String imageUrl) {
        if (entity == null) {
            return null;
        }
        return LinkModel.builder()
            .id(entity.getId())
            .linkType(entity.getLinkType())
            .linkUri(entity.getLinkUri())
            .imageId(entity.getImageId())
            .imageUrl(imageUrl)
            .description(entity.getDescription())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public long toTimestamp(LocalDateTime localDateTime) {
        return LocalDateTimes.toTimestamp(localDateTime, zoneId);
    }
}
