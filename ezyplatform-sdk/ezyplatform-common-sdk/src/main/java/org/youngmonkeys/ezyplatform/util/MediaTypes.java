/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.util;

import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.util.Locale;
import java.util.regex.Pattern;

public final class MediaTypes {

    private static final Pattern CUSTOM_MEDIA_TYPE_PATTERN =
        Pattern.compile("^[A-Z][A-Z0-9_]{0,48}$");

    private MediaTypes() {}

    public static boolean isCustomMediaType(String mediaType) {
        return mediaType != null
            && MediaType.ofNameOrNull(mediaType) == null
            && CUSTOM_MEDIA_TYPE_PATTERN.matcher(mediaType).matches();
    }

    public static String getMediaFolder(String mediaType) {
        MediaType builtInMediaType = MediaType.ofNameOrNull(mediaType);
        if (builtInMediaType != null) {
            return builtInMediaType.getFolder();
        }
        if (isCustomMediaType(mediaType)) {
            return mediaType.toLowerCase(Locale.ROOT) + "s";
        }
        return MediaType.FILE.getFolder();
    }
}
