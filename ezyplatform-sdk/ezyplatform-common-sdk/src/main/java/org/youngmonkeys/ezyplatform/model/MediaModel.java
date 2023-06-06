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

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

@Getter
@Builder
public class MediaModel {
    private long id;
    private String name;
    private String url;
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
    private long updatedAt;

    public static String getMediaUrlOrNull(
        MediaModel media
    ) {
        return getMediaUrlOrDefault(media, null);
    }

    public static String getMediaUrlOrDefault(
        MediaModel media,
        String defaultUrl
    ) {
        return media == null
            ? defaultUrl
            : media.getUrlOrDefault(defaultUrl);
    }

    public static String toMediaUrlOrDefault(
        String mediaName,
        String mediaUrl,
        String defaultUrl
    ) {
        return isBlank(mediaUrl)
            ? isBlank(mediaName) ? defaultUrl : "/api/v1/media/" + mediaName
            : mediaUrl;
    }

    public String getUrlOrDefault(String defaultUrl) {
        return toMediaUrlOrDefault(name, url, defaultUrl);
    }
}
