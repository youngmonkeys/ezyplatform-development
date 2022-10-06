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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;


@Getter
@Builder
public class MediaDetailsModel {
    private long size;
    private long width;
    private long height;
    private long id;
    private String name;
    private String originalName;
    private UploadFrom uploadFrom;
    private MediaType type;
    private String mimeType;
    private long ownerUserId;
    private long ownerAdminId;
    private String title;
    private String caption;
    private String alternativeText;
    private String description;
    private boolean publicMedia;
    private long createdAt;

    public static MediaDetailsModelBuilder from(MediaModel info) {
        return builder()
            .id(info.getId())
            .name(info.getName())
            .originalName(info.getOriginalName())
            .uploadFrom(info.getUploadFrom())
            .type(info.getType())
            .mimeType(info.getMimeType())
            .ownerUserId(info.getOwnerUserId())
            .ownerAdminId(info.getOwnerAdminId())
            .title(info.getTitle())
            .caption(info.getCaption())
            .alternativeText(info.getAlternativeText())
            .description(info.getDescription())
            .publicMedia(info.isPublicMedia())
            .createdAt(info.getCreatedAt());
    }
}
