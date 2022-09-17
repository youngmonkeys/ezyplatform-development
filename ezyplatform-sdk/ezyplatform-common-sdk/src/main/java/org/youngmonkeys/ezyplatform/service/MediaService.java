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

import com.tvd12.ezyfox.security.EzySHA256;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public interface MediaService {

    MediaModel addMedia(AddMediaModel model);

    MediaModel addMedia(
        AddMediaModel model,
        UploadFrom uploadFrom
    );

    void updateMedia(UpdateMediaModel model);

    void updateMedia(
        long userId,
        UpdateMediaModel model
    );

    void updateMedia(
        boolean byUser,
        long userId,
        UpdateMediaModel model
    );

    void updateMediaOwner(
        long mediaId,
        long ownerUserId
    );

    MediaModel removeMedia(long mediaId);

    MediaModel removeMedia(
        long userId,
        String mediaName
    );

    MediaModel removeMedia(
        boolean byUser,
        long userId,
        long mediaId,
        String mediaName
    );

    MediaModel getMediaById(long mediaId);

    MediaModel getMediaByName(String mediaName);

    String getMediaName(long mediaId);

    boolean containsMedia(long mediaId);

    List<MediaModel> getMediaListByIds(
        Collection<Long> mediaIds
    );

    default Map<Long, MediaModel> getMediaMapByIds(
        Collection<Long> mediaIds
    ) {
        return newHashMap(
            getMediaListByIds(mediaIds),
            MediaModel::getId
        );
    }

    default Map<Long, String> getMediaNameMapByIds(
        Collection<Long> mediaIds
    ) {
        return newHashMapNewValues(
            getMediaMapByIds(mediaIds),
            MediaModel::getName
        );
    }

    ImageSize getMediaImageSize(
        long mediaId
    ) throws IOException;

    ImageSize getMediaImageSize(
        String imageName,
        MediaType mediaType
    ) throws IOException;

    default ImageSize getMediaImageSize(
        String imageName
    ) throws IOException {
        return getMediaImageSize(imageName, MediaType.IMAGE);
    }

    default String generateMediaFileName() {
        return generateMediaFileName("", null);
    }

    default String generateMediaFileName(
        String submittedFileName,
        String extension
    ) {
        String uniqueFileName = EzySHA256.cryptUtfToLowercase(
            submittedFileName
                + System.currentTimeMillis()
                + UUID.randomUUID()
        );
        return isBlank(extension)
            ? uniqueFileName
            : uniqueFileName + "." + extension;
    }
}
