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

package org.youngmonkeys.ezyplatform.entity;

import com.tvd12.ezyfox.util.EzyEnums;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import lombok.Getter;

import java.util.Map;

@Getter
public enum MediaType {
    AUDIO("audio", "audio", "audios"),
    AVATAR("avatar", "avatar", "avatars"),
    DOCUMENT("application", "document", "files"),
    FONT("font", "font", "fonts"),
    FILE("file", "file", "files"),
    IMAGE("image", "image", "images"),
    VIDEO("video", "video", "videos");

    private final String name;

    private final String mimeTypeName;

    private final String folder;

    private static final Map<String, MediaType> MAP_BY_NAME =
        EzyMapBuilder
            .mapBuilder()
            .putAll(EzyEnums.enumMap(MediaType.class, it -> it.name))
            .putAll(EzyEnums.enumMap(MediaType.class, Enum::toString))
            .toMap();

    private static final Map<String, MediaType> MAP_BY_MIME_TYPE_NAME =
        EzyEnums.enumMap(MediaType.class, it -> it.name);

    MediaType(String mimeTypeName, String name, String folder) {
        this.name = name;
        this.mimeTypeName = mimeTypeName;
        this.folder = folder;
    }

    public static MediaType ofName(String name) {
        MediaType answer = name == null ? null : MAP_BY_NAME.get(name);
        return answer == null ? FILE : answer;
    }

    public static MediaType ofMimeTypeName(String name) {
        MediaType answer = name == null ? null : MAP_BY_MIME_TYPE_NAME.get(name);
        return answer == null ? FILE : answer;
    }

    public boolean equalsValue(String value) {
        return value != null && this.toString().equals(value);
    }
}
