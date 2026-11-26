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

package org.youngmonkeys.ezyplatform.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.util.MediaTypes;

import static org.youngmonkeys.ezyplatform.util.MediaTypes.isCustomMediaType;

@Getter
public class FileMetadata {
    private final String mimeType;
    private final String extension;
    private final MediaType mediaType;
    private final String customMediaType;
    private final long fileSize;

    protected FileMetadata(FileMetadataBuilder builder) {
        this.mimeType = builder.mimeType;
        this.extension = builder.extension;
        this.mediaType = builder.mediaType;
        this.customMediaType = builder.customMediaType;
        this.fileSize = builder.fileSize;
    }

    public MediaType getMediaType() {
        return mediaType != null
            ? mediaType
            : MediaType.FILE;
    }

    public String getMediaTypeText() {
        return isCustomMediaType(customMediaType)
            ? customMediaType
            : getMediaType().toString();
    }

    public String getMediaFolder() {
        return MediaTypes.getMediaFolder(getMediaTypeText());
    }

    public static FileMetadataBuilder builder() {
        return new FileMetadataBuilder();
    }

    public static class FileMetadataBuilder
        implements EzyBuilder<FileMetadata> {
        private String mimeType;
        private String extension;
        private MediaType mediaType;
        private String customMediaType;
        private long fileSize;

        public FileMetadataBuilder fileMetadata(FileMetadata fileMetadata) {
            this.mimeType = fileMetadata.mimeType;
            this.extension = fileMetadata.extension;
            this.mediaType = fileMetadata.mediaType;
            this.customMediaType = fileMetadata.customMediaType;
            this.fileSize = fileMetadata.fileSize;
            return this;
        }

        public FileMetadataBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public FileMetadataBuilder extension(String extension) {
            this.extension = extension;
            return this;
        }

        public FileMetadataBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public FileMetadataBuilder customMediaType(String customMediaType) {
            this.customMediaType = customMediaType;
            return this;
        }

        public FileMetadataBuilder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        @Override
        public FileMetadata build() {
            return new FileMetadata(this);
        }
    }
}
