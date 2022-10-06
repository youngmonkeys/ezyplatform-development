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
import org.youngmonkeys.ezyplatform.service.SettingService;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyhttp.core.constant.ContentType.getExtensionOfMimeType;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.DEFAULT_ACCEPTED_IMAGE_TYPES;
import static org.youngmonkeys.ezyplatform.entity.MediaType.AVATAR;
import static org.youngmonkeys.ezyplatform.entity.MediaType.ofMimeTypeName;
import static org.youngmonkeys.ezyplatform.validator.DefaultValidator.isValidMediaName;

@AllArgsConstructor
public class MediaValidator {

    private final TikaConfig tika;
    private final SettingService settingService;

    public void validateMediaName(String mediaName) {
        Map<String, String> errors = new HashMap<>();
        if (!isValidMediaName(mediaName)) {
            errors.put("mediaName", "invalid");
        }
        if (errors.size() > 0) {
            throw new HttpBadRequestException(errors);
        }
    }

    public FileMetadata validateFilePart(
        Part filePart,
        boolean avatar
    ) throws IOException {
        String extension = null;
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
            if (filePart.getSize() > settingService.getMaxUploadFileSize()) {
                errors.put("uploadSize", "over");
            }
            mediaType = tika.getDetector().detect(
                TikaInputStream.get(filePart.getInputStream()),
                new Metadata()
            );
            Set<String> acceptedMediaMimeTypes = settingService
                .getAcceptedMediaMimeTypes();
            if (!acceptedMediaMimeTypes.contains(mediaType.toString())
                && !acceptedMediaMimeTypes.contains("*")) {
                errors.put("fileType", "invalid");
            }
            if (avatar && !DEFAULT_ACCEPTED_IMAGE_TYPES.contains(mediaType.toString())) {
                errors.put("fileType", "invalid");
            }
        }
        if (errors.size() > 0) {
            throw new HttpBadRequestException(errors);
        }
        String mimeType = mediaType.toString();
        return FileMetadata.builder()
            .mimeType(mimeType)
            .extension(getExtensionOfMimeType(mimeType, extension))
            .mediaType(avatar ? AVATAR : ofMimeTypeName(mediaType.getType()))
            .build();
    }
}
