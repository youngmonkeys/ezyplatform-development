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

import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.request.AddMediaFromUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public class HttpRequestToModelConverter {

    public AddMediaModel toModel(
        long ownerAdminId,
        long ownerUserId,
        String mediaName,
        AddMediaFromUrlRequest request,
        boolean notPublic
    ) {
        return AddMediaModel.builder()
            .ownerAdminId(ownerAdminId)
            .ownerUserId(ownerUserId)
            .url(request.getUrl())
            .fileName(mediaName)
            .originalFileName(request.getOriginalName())
            .mediaType(request.getType())
            .mimeType(UNKNOWN_LOWERCASE)
            .notPublic(notPublic)
            .saveDuration(request.isSaveDuration())
            .durationInMinutes(request.getDurationInMinutes())
            .fileSize(request.getFileSize())
            .build();
    }

    public UpdateMediaModel toModel(
        long mediaId,
        UpdateMediaRequest request
    ) {
        return toModel(mediaId, NULL_STRING, request);
    }

    public UpdateMediaModel toModel(
        String mediaName,
        UpdateMediaRequest request
    ) {
        return toModel(ZERO_LONG, mediaName, request);
    }

    public UpdateMediaModel toModel(
        long mediaId,
        String mediaName,
        UpdateMediaRequest request
    ) {
        return UpdateMediaModel.builder()
            .mediaId(mediaId)
            .mediaName(mediaName)
            .alternativeText(request.getAlternativeText())
            .title(request.getTitle())
            .caption(request.getCaption())
            .description(request.getDescription())
            .fileSize(request.getFileSize())
            .notPublic(request.isNotPublic())
            .build();
    }

    public UpdateMediaModel toModel(
        long mediaId,
        UpdateMediaIncludeUrlRequest request
    ) {
        return UpdateMediaModel.builder()
            .mediaId(mediaId)
            .updateType(true)
            .type(request.getType())
            .alternativeText(request.getAlternativeText())
            .title(request.getTitle())
            .caption(request.getCaption())
            .description(request.getDescription())
            .fileSize(request.getFileSize())
            .notPublic(request.isNotPublic())
            .updateUrl(true)
            .url(request.getUrl())
            .updateDuration(request.isUpdateDuration())
            .durationInMinutes(request.getDurationInMinutes())
            .build();
    }
}
