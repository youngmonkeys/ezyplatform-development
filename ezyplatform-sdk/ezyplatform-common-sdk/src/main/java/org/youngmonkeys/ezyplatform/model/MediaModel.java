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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.entity.MediaType;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.util.Strings.from;

@Getter
@Builder
public class MediaModel {
    private long id;
    private String name;
    private String url;
    private String originalName;
    private String uploadFrom;
    private MediaType type;
    private String typeText;
    private String mimeType;
    private long ownerUserId;
    private long ownerAdminId;
    private String title;
    private String caption;
    private String alternativeText;
    private String description;
    private long fileSize;
    private boolean publicMedia;
    private String status;
    private long createdAt;
    private long updatedAt;

    public String getTypeText() {
        return isBlank(typeText)
            ? from(type)
            : typeText;
    }

    public static String getMediaUrlOrNull(
        MediaModel media
    ) {
        return getMediaUrlOrDefault(media, NULL_STRING);
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

    public static String toMeMediaUrlOrDefault(
        String mediaName,
        String mediaUrl,
        String defaultUrl
    ) {
        return isBlank(mediaUrl)
            ? isBlank(mediaName) ? defaultUrl : "/api/v1/me/media/" + mediaName
            : mediaUrl;
    }

    @JsonIgnore
    public String getUrlOrNull() {
        return getUrlOrDefault(NULL_STRING);
    }

    public String getUrlOrDefault(String defaultUrl) {
        return toMediaUrlOrDefault(name, url, defaultUrl);
    }

    public String getMeUrlOrDefault(String defaultUrl) {
        return toMeMediaUrlOrDefault(name, url, defaultUrl);
    }

    public String getOriginalName() {
        return isBlank(originalName) ? name : originalName;
    }
}
