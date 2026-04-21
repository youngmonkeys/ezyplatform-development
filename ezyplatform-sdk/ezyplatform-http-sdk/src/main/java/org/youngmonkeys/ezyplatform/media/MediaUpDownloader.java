/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.media;

import org.youngmonkeys.ezyplatform.model.MediaDetailsModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

public interface MediaUpDownloader {

    default void upload(
        MediaUploadArguments arguments
    ) {}

    default boolean isUploadSupported() {
        return Boolean.FALSE;
    }

    default long uploadFromUrl(
        MediaUploadFromUrlArguments arguments
    ) {
        return ZERO_LONG;
    }

    default boolean isUploadFromUrlSupported() {
        return Boolean.FALSE;
    }

    default void download(
        MediaDownloadArguments arguments
    ) {}

    default boolean isDownloadSupported() {
        return Boolean.FALSE;
    }

    default MediaDetailsModel getMediaDetails(
        MediaModel media
    ) {
        return null;
    }

    String getName();
}
