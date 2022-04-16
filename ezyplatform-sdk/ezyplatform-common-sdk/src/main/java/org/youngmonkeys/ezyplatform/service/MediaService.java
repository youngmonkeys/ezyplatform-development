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

import com.tvd12.ezyfox.sercurity.EzySHA256;
import org.youngmonkeys.ezyplatform.model.MediaModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public interface MediaService {

    MediaModel getMediaById(long mediaId);

    MediaModel getMediaByName(String mediaName);

    String getMediaName(long mediaId);

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

    default boolean containsMedia(long mediaId) {
        return getMediaById(mediaId) != null;
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
