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

package org.youngmonkeys.ezyplatform.validator;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import lombok.AllArgsConstructor;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.request.AddMediaFromUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.SettingService;

import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static com.tvd12.ezyhttp.core.constant.ContentType.getExtensionOfMimeType;
import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.entity.MediaType.AVATAR;
import static org.youngmonkeys.ezyplatform.entity.MediaType.ofMimeTypeName;
import static org.youngmonkeys.ezyplatform.validator.DefaultValidator.isValidMediaName;
import static org.youngmonkeys.ezyplatform.validator.DefaultValidator.isValidUrl;

@AllArgsConstructor
public class MediaValidator {

    private final TikaConfig tika;
    private final MediaService mediaService;
    private final SettingService settingService;

    public MediaModel validateMediaId(long mediaId) {
        MediaModel media = mediaService.getMediaById(
            mediaId
        );
        if (media == null) {
            throw new ResourceNotFoundException("media");
        }
        return media;
    }

    public void validateOwnerAdminMedia(
        long adminId,
        long mediaId
    ) {
        long ownerId = mediaService
            .getOwnerAdminIdByMediaId(mediaId);
        if (ownerId != adminId) {
            throw new MediaNotFoundException(mediaId);
        }
    }

    public void validateOwnerAdminMedia(
        long adminId,
        String mediaName
    ) {
        long ownerId = mediaService
            .getOwnerAdminIdByMediaName(mediaName);
        if (ownerId != adminId) {
            throw new MediaNotFoundException(mediaName);
        }
    }

    public void validateOwnerUserMedia(
        long userId,
        long mediaId
    ) {
        long ownerId = mediaService
            .getOwnerUserIdByMediaId(mediaId);
        if (ownerId != userId) {
            throw new MediaNotFoundException(mediaId);
        }
    }

    public void validateOwnerUserMedia(
        long userId,
        String mediaName
    ) {
        long ownerId = mediaService
            .getOwnerUserIdByMediaName(mediaName);
        if (ownerId != userId) {
            throw new MediaNotFoundException(mediaName);
        }
    }

    public MediaModel validateUserMedia(
        long userId,
        long mediaId
    ) {
        MediaModel media = validateMediaId(mediaId);
        if (media.getOwnerUserId() != userId) {
            throw new ResourceNotFoundException("media");
        }
        return media;
    }

    public void validateMediaName(String mediaName) {
        Map<String, String> errors = new HashMap<>();
        if (!isValidMediaName(mediaName)) {
            errors.put("mediaName", "invalid");
        }
        if (!errors.isEmpty()) {
            throw new HttpBadRequestException(errors);
        }
    }

    public MediaModel validateMediaNameAndGet(
        String mediaName
    ) {
        MediaModel media = mediaService.getMediaByName(
            mediaName
        );
        if (media == null) {
            throw new ResourceNotFoundException("media");
        }
        return media;
    }

    public FileMetadata validateFilePart(
        Part filePart,
        boolean avatar
    ) throws IOException {
        long fileSize = ZERO_LONG;
        String extension = null;
        String mimeType = null;
        org.apache.tika.mime.MediaType mediaType = null;
        Map<String, String> errors = new HashMap<>();
        if (filePart == null) {
            errors.put("file", "invalid");
        } else {
            String fileName = filePart.getSubmittedFileName();
            if (EzyStrings.isNoContent(fileName)) {
                errors.put("fileName", "invalid");
            }
            extension = EzyFileUtil.getFileExtension(fileName);
            if (EzyStrings.isNoContent(extension)) {
                errors.put("fileType", "invalid");
            }
            fileSize = filePart.getSize();
            if (fileSize > settingService.getMaxUploadFileSize()) {
                errors.put("uploadSize", "over");
            }
            try (TikaInputStream tikaInputStream =
                     TikaInputStream.get(filePart.getInputStream())
            ) {
                Metadata metadata = new Metadata();
                metadata.set(RESOURCE_NAME_KEY, fileName);
                mediaType = tika.getDetector().detect(
                    tikaInputStream,
                    metadata
                );
            }
            mimeType = mediaType.toString();
            Set<String> acceptedMediaMimeTypes = settingService
                .getAcceptedMediaMimeTypes();
            if (!acceptedMediaMimeTypes.contains(mimeType)
                && !acceptedMediaMimeTypes.contains("*")) {
                errors.put("fileType", "invalid");
            }
        }
        if (!errors.isEmpty()) {
            throw new HttpBadRequestException(errors);
        }
        return FileMetadata.builder()
            .mimeType(mimeType)
            .fileSize(fileSize)
            .extension(getExtensionOfMimeType(mimeType, extension))
            .mediaType(avatar ? AVATAR : ofMimeTypeName(mediaType.getType()))
            .build();
    }

    public void validate(AddMediaFromUrlRequest request) {
        Map<String, String> errors = new HashMap<>();
        MediaType type = request.getType();
        if (type == null) {
            errors.put("type", "required");
        }
        String originalName = request.getOriginalName();
        if (isBlank(originalName)) {
            errors.put("originalName", "required");
        } else if (originalName.length() > MAX_MEDIA_ORIGINAL_NAME_LENGTH) {
            errors.put("originalName", "overLength");
        }
        String url = request.getUrl();
        if (isBlank(url)) {
            errors.put("url", "required");
        } else if (url.length() > MAX_MEDIA_URL_LENGTH) {
            errors.put("url", "overLength");
        } else if (!isValidUrl(url)) {
            errors.put("url", "invalid");
        }
        BigDecimal durationInMinutes = request.getDurationInMinutes();
        if (durationInMinutes != null
            && durationInMinutes.compareTo(BigDecimal.ZERO) < ZERO
        ) {
            errors.put("durationInMinutes", "invalid");
        }
        if (!errors.isEmpty()) {
            throw new HttpBadRequestException(errors);
        }
    }

    public void validate(UpdateMediaRequest request) {
        Map<String, String> errors = new HashMap<>();
        String alternativeText = request.getAlternativeText();
        if (alternativeText != null
            && alternativeText.length() > MAX_MEDIA_ALT_TEXT_LENGTH
        ) {
            errors.put("alternativeText", "overLength");
        }
        String title = request.getTitle();
        if (title != null
            && title.length() > MAX_MEDIA_TITLE_LENGTH
        ) {
            errors.put("title", "overLength");
        }
        String caption = request.getCaption();
        if (caption != null
            && caption.length() > MAX_MEDIA_CAPTION_LENGTH
        ) {
            errors.put("caption", "overLength");
        }
        String description = request.getDescription();
        if (description != null
            && description.length() > MAX_MEDIA_DESCRIPTION_LENGTH
        ) {
            errors.put("description", "overLength");
        }
        if (!errors.isEmpty()) {
            throw new HttpBadRequestException(errors);
        }
    }

    public void validate(UpdateMediaIncludeUrlRequest request) {
        Map<String, String> errors = new HashMap<>();
        String alternativeText = request.getAlternativeText();
        if (alternativeText != null
            && alternativeText.length() > MAX_MEDIA_ALT_TEXT_LENGTH
        ) {
            errors.put("alternativeText", "overLength");
        }
        String title = request.getTitle();
        if (title != null
            && title.length() > MAX_MEDIA_TITLE_LENGTH
        ) {
            errors.put("title", "overLength");
        }
        String caption = request.getCaption();
        if (caption != null
            && caption.length() > MAX_MEDIA_CAPTION_LENGTH
        ) {
            errors.put("caption", "overLength");
        }
        String description = request.getDescription();
        if (description != null
            && description.length() > MAX_MEDIA_DESCRIPTION_LENGTH
        ) {
            errors.put("description", "overLength");
        }
        String url = request.getUrl();
        if (isNotBlank(url)) {
            if (url.length() > MAX_MEDIA_URL_LENGTH) {
                errors.put("url", "overLength");
            } else if (!isValidUrl(url)) {
                errors.put("url", "invalid");
            }
        }
        if (request.isUpdateDuration()) {
            BigDecimal durationInMinutes = request.getDurationInMinutes();
            if (durationInMinutes != null
                && durationInMinutes.compareTo(BigDecimal.ZERO) < ZERO
            ) {
                errors.put("durationInMinutes", "invalid");
            }
        }
        if (!errors.isEmpty()) {
            throw new HttpBadRequestException(errors);
        }
    }
}
