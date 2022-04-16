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
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.repo.MediaRepository;

import java.util.Collection;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultMediaService implements MediaService {

    private final MediaRepository mediaRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    public MediaModel addMedia(AddMediaModel model) {
        return addMedia(model, UploadFrom.ADMIN);
    }

    public MediaModel addMedia(AddMediaModel model, UploadFrom uploadFrom) {
        Media entity = modelToEntityConverter.toEntity(model, uploadFrom);
        mediaRepository.save(entity);
        return entityToModelConverter.toModel(entity);
    }

    public void updateMedia(UpdateMediaModel model) {
        updateMedia(false, 0L, model);
    }

    public void updateMedia(long userId, UpdateMediaModel model) {
        updateMedia(true, userId, model);
    }

    public void updateMedia(boolean byUser, long userId, UpdateMediaModel model) {
        Media entity = model.getMediaId() > 0
            ? mediaRepository.findById(model.getMediaId())
            : mediaRepository.findByField("name", model.getMediaName());
        if (entity == null || (byUser && entity.getOwnerUserId() != userId)) {
            throw new MediaNotFoundException(model.getMediaId(), model.getMediaName());
        }
        modelToEntityConverter.mergeToEntity(model, entity);
        mediaRepository.save(entity);
    }

    public MediaModel removeMedia(long mediaId) {
        return removeMedia(false, 0L, mediaId, null);
    }

    public MediaModel removeMedia(long userId, String mediaName) {
        return removeMedia(true, userId, 0L, mediaName);
    }

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
    public String getMediaName(long mediaId) {
        if (mediaId <= 0) {
            return null;
        }
        return mediaRepository.findByIdOptional(mediaId)
            .map(Media::getName)
            .orElse(null);
    }

    @Override
    public List<MediaModel> getMediaListByIds(
        Collection<Long> mediaIds
    ) {
        return newArrayList(
            mediaRepository.findListByIds(mediaIds),
            entityToModelConverter::toModel
        );
    }
}
