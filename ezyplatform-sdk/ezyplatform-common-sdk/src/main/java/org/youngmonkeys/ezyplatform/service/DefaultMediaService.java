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

import com.tvd12.ezyfox.io.EzyStrings;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.entity.MediaStatus;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.io.ImageProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.repo.MediaRepository;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.result.StatusResult;
import org.youngmonkeys.ezyplatform.result.TypeResult;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_MEDIA;

@AllArgsConstructor
public class DefaultMediaService implements MediaService {

    private final FileSystemManager fileSystemManager;
    private final UniqueDataService uniqueDataService;
    private final MediaRepository mediaRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public MediaModel addMedia(
        String uploadFrom,
        AddMediaModel model
    ) {
        Media entity = modelToEntityConverter.toEntity(
            model,
            uploadFrom
        );
        mediaRepository.save(entity);
        long mediaId = entity.getId();
        if (model.isSaveDuration()) {
            saveMediaDurationInMinutes(
                mediaId,
                model.getDurationInMinutes()
            );
        }
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public void updateMedia(
        UpdateMediaModel model
    ) {
        long mediaId = model.getMediaId();
        String mediaName = model.getMediaName();
        Media entity = model.getMediaId() > ZERO_LONG
            ? mediaRepository.findById(mediaId)
            : mediaRepository.findByNameOrOriginalName(mediaName);
        if (entity == null) {
            throw new MediaNotFoundException(
                mediaId,
                mediaName
            );
        }
        modelToEntityConverter.mergeToEntity(model, entity);
        mediaRepository.save(entity);
        if (model.isUpdateDuration()) {
            saveMediaDurationInMinutes(
                mediaId,
                model.getDurationInMinutes()
            );
        }
    }

    @Override
    public MediaModel replaceMedia(
        ReplaceMediaModel model
    ) {
        Media entity = getMediaEntityByIdOrThrow(
            model.getMediaId()
        );
        modelToEntityConverter.mergeToEntity(model, entity);
        mediaRepository.save(entity);
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public void saveMediaDurationInMinutes(
        long mediaId,
        BigDecimal duration
    ) {
        uniqueDataService.saveDataMeta(
            UniqueDataModel.builder()
                .dataType(TABLE_NAME_MEDIA)
                .dataId(mediaId)
                .uniqueKey(META_KEY_DURATION_IN_MINUTES)
                .decimalValue(
                    duration != null ? duration : BigDecimal.ZERO
                )
                .build()
        );
    }

    @Override
    public void updateMediaOwner(
        long mediaId,
        long ownerUserId
    ) {
        mediaRepository.updateOwnerUserId(
            mediaId,
            ownerUserId
        );
    }

    @Override
    public MediaModel removeMedia(long mediaId) {
        Media entity = getMediaEntityByIdOrThrow(mediaId);
        removeMediaEntity(entity);
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public MediaModel removeMedia(String mediaName) {
        Media entity = getMediaEntityByNameOrThrow(mediaName);
        removeMediaEntity(entity);
        return entityToModelConverter.toModel(entity);
    }

    private void removeMediaEntity(Media entity) {
        if (MediaStatus.REMOVED.equalsValue(entity.getStatus())) {
            entity.setStatus(DELETED);
            mediaRepository.delete(entity.getId());
        } else {
            entity.setStatus(MediaStatus.REMOVED.toString());
            modelToEntityConverter.mergeUpdatedAtToEntity(entity);
            mediaRepository.save(entity);
        }
    }

    @Override
    public boolean containsMedia(long mediaId) {
        return mediaRepository.containsById(mediaId);
    }

    @Override
    public List<String> getAllMediaTypes() {
        return mediaRepository
            .findAllMediaTypes()
            .stream()
            .map(TypeResult::getType)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMediaStatuses() {
        return mediaRepository
            .findAllMediaStatuses()
            .stream()
            .map(StatusResult::getStatus)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public MediaModel getMediaById(long mediaId) {
        return entityToModelConverter.toModel(
            mediaRepository.findById(mediaId)
        );
    }

    @Override
    public MediaModel getMediaByName(String mediaName) {
        return entityToModelConverter.toModel(
            mediaRepository.findByNameOrOriginalName(mediaName)
        );
    }

    @Override
    public Map<Long, MediaModel> getMediaMapByIds(
        Collection<Long> mediaIds
    ) {
        if (mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return mediaRepository.findListByIds(mediaIds)
            .stream()
            .collect(
                Collectors.toMap(
                    Media::getId,
                    entityToModelConverter::toModel,
                    (o, n) -> n
                )
            );
    }

    @Override
    public long getMediaFileLength(
        MediaType mediaType,
        String mediaName
    ) {
        long fileLength = getMediaFileLengthOrNegative(
            mediaType,
            mediaName
        );
        if (fileLength >= ZERO_LONG) {
            return fileLength;
        }
        throw new ResourceNotFoundException("media");
    }

    @Override
    public long getMediaFileLengthOrNegative(
        MediaType mediaType,
        String mediaName
    ) {
        File mediaFile = fileSystemManager.getMediaFilePath(
            mediaType.getFolder(),
            mediaName
        );
        if (mediaFile.exists()) {
            return mediaFile.length();
        }
        return -1L;
    }

    @Override
    public long getMediaFileLengthOrZero(File mediaFile) {
        if (mediaFile.exists()) {
            return mediaFile.length();
        }
        return ZERO_LONG;
    }


    @Override
    public ImageSize getMediaImageSize(
        String imageName,
        MediaType mediaType
    ) {
        ImageSize imageSize = getMediaImageSizeOrNull(
            imageName,
            mediaType
        );
        if (imageSize != null) {
            return imageSize;
        }
        throw new ResourceNotFoundException("media");
    }

    @Override
    public ImageSize getMediaImageSize(
        long mediaId
    ) {
        Media media = mediaRepository.findById(mediaId);
        if (media == null) {
            throw new ResourceNotFoundException("media");
        }
        return getMediaImageSize(
            media.getName(),
            MediaType.ofName(media.getType())
        );
    }

    @Override
    public ImageSize getMediaImageSizeOrNull(
        String imageName,
        MediaType mediaType
    ) {
        File mediaFile = fileSystemManager.getMediaFilePath(
            mediaType.getFolder(),
            imageName
        );
        if (mediaFile.exists()) {
            return ImageProxy.getImageSize(mediaFile);
        }
        return null;
    }

    @Override
    public ImageSize getMediaImageSizeOrDefault(
        File mediaFile
    ) {
        if (mediaFile.exists()) {
            return ImageProxy.getImageSize(mediaFile);
        }
        return ImageSize.ZERO;
    }

    @Override
    public BigDecimal getMediaDurationInMinutes(long mediaId) {
        return uniqueDataService.getUniqueDataValueByDataTypeAndDataIdAndUniqueKey(
            TABLE_NAME_MEDIA,
            mediaId,
            META_KEY_DURATION_IN_MINUTES,
            UniqueDataModel::getDecimalValue,
            BigDecimal.ZERO
        );
    }

    @Override
    public Map<Long, BigDecimal> getMediaDurationInMinutesByIds(
        Collection<Long> mediaIds
    ) {
        return uniqueDataService.getUniqueDataValueMapByDataTypeAndDataIdsAndUniqueKey(
            TABLE_NAME_MEDIA,
            mediaIds,
            META_KEY_DURATION_IN_MINUTES,
            UniqueDataModel::getDecimalValue
        );
    }

    @Override
    public long getOwnerAdminIdByMediaId(long mediaId) {
        IdResult result = mediaRepository
            .findOwnerAdminIdById(mediaId);
        return result != null ? result.getId() : ZERO_LONG;
    }

    @Override
    public long getOwnerAdminIdByMediaName(String mediaName) {
        IdResult result = mediaRepository
            .findOwnerAdminIdByNameOrOriginalName(mediaName);
        return result != null ? result.getId() : ZERO_LONG;
    }

    @Override
    public long getOwnerUserIdByMediaId(long mediaId) {
        IdResult result = mediaRepository
            .findOwnerUserIdById(mediaId);
        return result != null ? result.getId() : ZERO_LONG;
    }

    @Override
    public long getOwnerUserIdByMediaName(String mediaName) {
        IdResult result = mediaRepository
            .findOwnerUserIdByNameOrOriginalName(mediaName);
        return result != null ? result.getId() : ZERO_LONG;
    }

    protected Media getMediaEntityByIdOrThrow(
        long mediaId
    ) {
        Media entity = mediaRepository.findById(mediaId);
        if (entity == null) {
            throw new MediaNotFoundException(mediaId);
        }
        return entity;
    }

    protected Media getMediaEntityByNameOrThrow(
        String mediaName
    ) {
        Media entity = mediaRepository
            .findByNameOrOriginalName(mediaName);
        if (entity == null) {
            throw new MediaNotFoundException(mediaName);
        }
        return entity;
    }
}
