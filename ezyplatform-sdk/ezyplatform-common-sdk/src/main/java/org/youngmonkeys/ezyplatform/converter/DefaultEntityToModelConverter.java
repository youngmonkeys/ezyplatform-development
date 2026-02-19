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
            .tokenType(entity.getTokenType())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .expiredAt(entity.getExpiredAt())
            .createdAtTimestamp(toTimestamp(entity.getCreatedAt()))
            .expiredAtTimestamp(toTimestamp(entity.getExpiredAt()))
            .build();
    }

    public MediaModel toModel(Media entity) {
        if (entity == null) {
            return null;
        }
        String type = entity.getType();
        return MediaModel.builder()
            .id(entity.getId())
            .name(entity.getName())
            .url(entity.getUrl())
            .originalName(entity.getOriginalName())
            .type(MediaType.ofName(type))
            .typeText(type)
            .mimeType(entity.getMimeType())
            .uploadFrom(entity.getUploadFrom())
            .ownerUserId(entity.getOwnerUserId())
            .ownerAdminId(entity.getOwnerAdminId())
            .title(entity.getTitle())
            .caption(entity.getCaption())
            .alternativeText(entity.getAlternativeText())
            .description(entity.getDescription())
            .publicMedia(entity.isPublicMedia())
            .status(entity.getStatus())
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
            .tokenType(entity.getTokenType())
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
            .priority(entity.getPriority())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public UserKeywordModel toModel(UserKeyword entity) {
        if (entity == null) {
            return null;
        }
        return UserKeywordModel.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .keyword(entity.getKeyword())
            .priority(entity.getPriority())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public DataIndexModel toModel(DataIndex entity) {
        if (entity == null) {
            return null;
        }
        return DataIndexModel.builder()
            .id(entity.getId())
            .dataType(entity.getDataType())
            .dataId(entity.getDataId())
            .keyword(entity.getKeyword())
            .priority(entity.getPriority())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public SimpleNotificationModel toModel(Notification entity) {
        if (entity == null) {
            return null;
        }
        return SimpleNotificationModel.builder()
            .id(entity.getId())
            .type(entity.getType())
            .title(entity.getTitle())
            .content(entity.getContent())
            .contentType(entity.getContentType())
            .iconImage(entity.getIconImage())
            .deepLink(entity.getDeepLink())
            .fromAdminId(entity.getFromAdminId())
            .fromUserId(entity.getFromUserId())
            .status(entity.getStatus())
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
            .contentType(notification.getContentType())
            .iconImage(notification.getIconImage())
            .deepLink(notification.getDeepLink())
            .status(notification.getStatus())
            .fromAdminId(includeFromAdmin ? notification.getFromAdminId() : 0L)
            .fromUserId(notification.getFromUserId())
            .toAdminId(notificationReceiver.getToAdminId())
            .toUserId(notificationReceiver.getToUserId())
            .notificationReceiverId(notificationReceiver.getId())
            .confidenceLevel(notificationReceiver.getConfidenceLevel())
            .importantLevel(notificationReceiver.getImportantLevel())
            .receiveStatus(notificationReceiver.getStatus())
            .sentAt(toTimestamp(notificationReceiver.getSentAt()))
            .receivedAt(toTimestamp(notificationReceiver.getReceivedAt()))
            .readAt(toTimestamp(notificationReceiver.getReadAt()))
            .build();
    }

    public NotificationReceiverModel toModel(
        NotificationReceiver entity
    ) {
        if (entity == null) {
            return null;
        }
        return NotificationReceiverModel.builder()
            .id(entity.getId())
            .notificationId(entity.getNotificationId())
            .toAdminId(entity.getToAdminId())
            .toUserId(entity.getToUserId())
            .confidenceLevel(entity.getConfidenceLevel())
            .importantLevel(entity.getImportantLevel())
            .status(entity.getStatus())
            .sentAt(toTimestamp(entity.getSentAt()))
            .receivedAt(toTimestamp(entity.getReceivedAt()))
            .readAt(toTimestamp(entity.getReadAt()))
            .build();
    }

    public SimpleLetterModel toModel(Letter entity) {
        if (entity == null) {
            return null;
        }
        return SimpleLetterModel.builder()
            .id(entity.getId())
            .type(entity.getType())
            .title(entity.getTitle())
            .content(entity.getContent())
            .contentType(entity.getContentType())
            .fromAdminId(entity.getFromAdminId())
            .fromUserId(entity.getFromUserId())
            .parentId(entity.getParentId())
            .status(entity.getStatus())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .build();
    }

    public LetterModel toModel(
        Letter letter,
        LetterReceiver letterReceiver
    ) {
        return LetterModel.builder()
            .id(letter.getId())
            .title(letter.getTitle())
            .content(letter.getContent())
            .contentType(letter.getContentType())
            .status(letter.getStatus())
            .fromAdminId(letter.getFromAdminId())
            .fromUserId(letter.getFromUserId())
            .toAdminId(letterReceiver.getToAdminId())
            .toUserId(letterReceiver.getToUserId())
            .parentId(letter.getParentId())
            .letterReceiverId(letterReceiver.getId())
            .notificationReceiverId(letterReceiver.getNotificationReceiverId())
            .confidenceLevel(letterReceiver.getConfidenceLevel())
            .importantLevel(letterReceiver.getImportantLevel())
            .receiveStatus(letterReceiver.getStatus())
            .sentAt(toTimestamp(letterReceiver.getSentAt()))
            .receivedAt(toTimestamp(letterReceiver.getReceivedAt()))
            .readAt(toTimestamp(letterReceiver.getReadAt()))
            .build();
    }

    public LetterReceiverModel toModel(LetterReceiver entity) {
        if (entity == null) {
            return null;
        }
        return LetterReceiverModel.builder()
            .id(entity.getId())
            .letterId(entity.getLetterId())
            .toAdminId(entity.getToAdminId())
            .toUserId(entity.getToUserId())
            .notificationReceiverId(entity.getNotificationReceiverId())
            .confidenceLevel(entity.getConfidenceLevel())
            .importantLevel(entity.getImportantLevel())
            .status(entity.getStatus())
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
            .ownerType(entity.getOwnerType())
            .ownerId(entity.getOwnerId())
            .type(entity.getTemplateType())
            .name(entity.getTemplateName())
            .titleTemplate(entity.getTitleTemplate())
            .contentTemplate(entity.getContentTemplate())
            .contentType(entity.getContentType())
            .creatorType(entity.getCreatorType())
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
            .sourceType(entity.getSourceType())
            .sourceId(entity.getSourceId())
            .createdAt(toTimestamp(entity.getCreatedAt()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public DataMappingModel toModel(DataMapping entity) {
        if (entity == null) {
            return null;
        }
        LocalDateTime mappedAt = entity.getMappedAt();
        return DataMappingModel.builder()
            .mappingName(entity.getMappingName())
            .fromDataId(entity.getFromDataId())
            .toDataId(entity.getToDataId())
            .displayOrder(entity.getDisplayOrder())
            .quantity(entity.getQuantity())
            .remainingQuantity(entity.getQuantity())
            .numberData(entity.getNumberData())
            .decimalData(entity.getDecimalData())
            .textData(entity.getTextData())
            .metadata(entity.getMetadata())
            .mappedAt(toTimestamp(mappedAt))
            .mappedAtLocalDataTime(mappedAt)
            .build();
    }

    public UniqueDataModel toModel(UniqueData entity) {
        if (entity == null) {
            return  null;
        }
        return UniqueDataModel.builder()
            .dataType(entity.getDataType())
            .dataId(entity.getDataId())
            .uniqueKey(entity.getUniqueKey())
            .textValue(entity.getTextValue())
            .numberValue(entity.getNumberValue())
            .decimalValue(entity.getDecimalValue())
            .metadata(entity.getMetadata())
            .build();
    }

    public AdminMetaModel toModel(AdminMeta entity) {
        if (entity == null) {
            return null;
        }
        return AdminMetaModel.builder()
            .id(entity.getId())
            .adminId(entity.getAdminId())
            .metaKey(entity.getMetaKey())
            .metaValue(entity.getMetaValue())
            .metaNumberValue(entity.getMetaNumberValue())
            .metaTextValue(entity.getMetaTextValue())
            .build();
    }

    public DataMetaModel toModel(DataMeta entity) {
        if (entity == null) {
            return null;
        }
        return DataMetaModel.builder()
            .id(entity.getId())
            .dataType(entity.getDataType())
            .dataId(entity.getDataId())
            .metaKey(entity.getMetaKey())
            .metaValue(entity.getMetaValue())
            .metaNumberValue(entity.getMetaNumberValue())
            .metaTextValue(entity.getMetaTextValue())
            .build();
    }

    public UserMetaModel toModel(UserMeta entity) {
        if (entity == null) {
            return null;
        }
        return UserMetaModel.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .metaKey(entity.getMetaKey())
            .metaValue(entity.getMetaValue())
            .metaNumberValue(entity.getMetaNumberValue())
            .metaTextValue(entity.getMetaTextValue())
            .build();
    }

    public AccessTokenMetaModel toModel(AccessTokenMeta entity) {
        if (entity == null) {
            return null;
        }
        return AccessTokenMetaModel.builder()
            .id(entity.getId())
            .target(entity.getTarget())
            .accessToken(entity.getAccessToken())
            .accessTokenFull(entity.getAccessTokenFull())
            .parentId(entity.getParentId())
            .tokenType(entity.getTokenType())
            .algorithm(entity.getAlgorithm())
            .scope(entity.getScope())
            .issuer(entity.getIssuer())
            .tenantId(entity.getTenantId())
            .clientId(entity.getClientId())
            .deviceId(entity.getDeviceId())
            .clientSecret(entity.getClientSecret())
            .grantType(entity.getGrantType())
            .kid(entity.getKid())
            .jwksUri(entity.getJwksUri())
            .publicKey(entity.getPublicKey())
            .privateKey(entity.getPrivateKey())
            .audience(entity.getAudience())
            .notBefore(toTimestamp(entity.getNotBefore()))
            .updatedAt(toTimestamp(entity.getUpdatedAt()))
            .build();
    }

    public long toTimestamp(LocalDateTime localDateTime) {
        return LocalDateTimes.toTimestamp(localDateTime, zoneId);
    }
}
