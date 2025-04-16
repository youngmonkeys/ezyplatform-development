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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.io.ImageProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.model.UniqueDataModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.repo.MediaRepository;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.META_KEY_DURATION_IN_MINUTES;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_MEDIA;

@AllArgsConstructor
public class DefaultMediaService implements MediaService {

    private final FileSystemManager fileSystemManager;
    private final UniqueDataService uniqueDataService;
    private final MediaRepository mediaRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public MediaModel addMedia(AddMediaModel model) {
        return addMedia(model, UploadFrom.ADMIN);
    }

    @Override
    public MediaModel addMedia(AddMediaModel model, UploadFrom uploadFrom) {
        Media entity = modelToEntityConverter.toEntity(model, uploadFrom);
        mediaRepository.save(entity);
        long mediaId = entity.getId();
        if (model.isSaveDuration()) {
            saveMediaDurationInMinutes(mediaId, model.getDurationInMinutes());
        }
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public void updateMedia(UpdateMediaModel model) {
        updateMedia(false, 0L, model);
    }

    @Override
    public void updateMedia(long userId, UpdateMediaModel model) {
        updateMedia(true, userId, model);
    }

    @Override
    public void updateMedia(boolean byUser, long userId, UpdateMediaModel model) {
        Media entity = model.getMediaId() > 0
            ? mediaRepository.findById(model.getMediaId())
            : mediaRepository.findByField("name", model.getMediaName());
        if (entity == null || (byUser && entity.getOwnerUserId() != userId)) {
            throw new MediaNotFoundException(model.getMediaId(), model.getMediaName());
        }
        modelToEntityConverter.mergeToEntity(model, entity);
        mediaRepository.save(entity);
        long mediaId = entity.getId();
        if (model.isUpdateDuration()) {
            saveMediaDurationInMinutes(mediaId, model.getDurationInMinutes());
        }
    }

    @Override
    public void saveMediaDurationInMinutes(long mediaId, BigDecimal duration) {
        uniqueDataService.saveDataMeta(
            UniqueDataModel.builder()
                .dataType(TABLE_NAME_MEDIA)
                .dataId(mediaId)
                .uniqueKey(META_KEY_DURATION_IN_MINUTES)
                .decimalValue(duration != null ? duration : BigDecimal.ZERO)
                .build()
        );
    }

    @Override
    public void updateMediaOwner(long mediaId, long ownerUserId) {
        mediaRepository.updateOwnerUserId(mediaId, ownerUserId);
    }

    @Override
    public MediaModel removeMedia(long mediaId) {
        return removeMedia(false, 0L, mediaId, null);
    }

    @Override
    public MediaModel removeMedia(long userId, String mediaName) {
        return removeMedia(true, userId, 0L, mediaName);
    }

    @Override
    public MediaModel removeMedia(
        boolean byUser,
        long userId,
        long mediaId,
        String mediaName
    ) {
        Media entity = mediaId > 0
            ? mediaRepository.findById(mediaId)
            : mediaRepository.findByField("name", mediaName);
        if (entity == null || (byUser && entity.getOwnerUserId() != userId)) {
            throw new MediaNotFoundException(mediaId, mediaName);
        }
        mediaRepository.delete(entity.getId());
        return entityToModelConverter.toModel(entity);
    }

    @Override
    public boolean containsMedia(long mediaId) {
        return mediaRepository.containsById(mediaId);
    }

    @Override
    public MediaModel getMediaById(long mediaId) {
        if (mediaId <= 0) {
            return null;
        }
        return mediaRepository.findByIdOptional(mediaId)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public MediaModel getMediaByName(String mediaName) {
        return mediaRepository.findByFieldOptional("name", mediaName)
            .map(entityToModelConverter::toModel)
            .orElse(null);
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
        if (fileLength >= 0) {
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
        return 0L;
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
        if (mediaId <= 0) {
            throw new ResourceNotFoundException("media");
        }
        Media media = mediaRepository.findById(mediaId);
        if (media == null) {
            throw new ResourceNotFoundException("media");
        }
        return getMediaImageSize(
            media.getName(),
            media.getType()
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
}
