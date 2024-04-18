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

import static org.youngmonkeys.ezyplatform.model.MediaModel.toMeMediaUrlOrDefault;
import static org.youngmonkeys.ezyplatform.model.MediaModel.toMediaUrlOrDefault;

@Getter
@Builder
public class MediaNameModel {
    private long id;
    private String name;
    private String originalName;
    private String url;

    public static MediaNameModel fromMediaModel(
        MediaModel model
    ) {
        if (model == null) {
            return null;
        }
        return MediaNameModel.builder()
            .id(model.getId())
            .name(model.getName())
            .originalName(model.getOriginalName())
            .url(model.getUrl())
            .build();
    }

    public static String getMediaNameOrNull(
        MediaNameModel media
    ) {
        return getMediaNameOrDefault(media, null);
    }

    public static String getMediaNameOrDefault(
        MediaNameModel media,
        String defaultName
    ) {
        return media == null
            ? defaultName
            : media.getName();
    }

    public static String getMediaUrlOrNull(
        MediaNameModel media
    ) {
        return getMediaUrlOrDefault(media, null);
    }

    public static String getMediaUrlOrDefault(
        MediaNameModel media,
        String defaultUrl
    ) {
        return media == null
            ? defaultUrl
            : media.getUrlOrDefault(defaultUrl);
    }

    public String getUrlOrNull() {
        return getUrlOrDefault(null);
    }

    public String getUrlOrDefault(String defaultUrl) {
        return toMediaUrlOrDefault(name, url, defaultUrl);
    }

    public String getMeUrlOrDefault(String defaultUrl) {
        return toMeMediaUrlOrDefault(name, url, defaultUrl);
    }
}
