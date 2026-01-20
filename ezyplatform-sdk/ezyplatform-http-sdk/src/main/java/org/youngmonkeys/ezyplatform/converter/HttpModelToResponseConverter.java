/*
 * Copyright 2023 youngmonkeys.org
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

import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.response.MediaResponse;

public class HttpModelToResponseConverter extends DefaultModelToResponseConverter {

    public MediaResponse toResponse(MediaModel model) {
        return MediaResponse.builder()
            .id(model.getId())
            .name(model.getName())
            .url(model.getUrl())
            .originalName(model.getOriginalName())
            .uploadFrom(model.getUploadFrom())
            .type(model.getTypeText())
            .mimeType(model.getMimeType())
            .title(model.getTitle())
            .caption(model.getCaption())
            .alternativeText(model.getAlternativeText())
            .description(model.getDescription())
            .publicMedia(model.isPublicMedia())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
}
