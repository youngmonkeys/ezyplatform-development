/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.event;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.model.MediaModel;

@Getter
public class MediaUploadEvent {
    private final long byAdminId;
    private final long byUserId;
    private final String uploadFrom;
    private final long ownerAdminId;
    private final long ownerUserId;
    private final MediaModel media;
    private final FileMetadata fileMetadata;

    protected MediaUploadEvent(Builder builder) {
        this.byAdminId = builder.byAdminId;
        this.byUserId = builder.byUserId;
        this.uploadFrom = builder.uploadFrom;
        this.ownerAdminId = builder.ownerAdminId;
        this.ownerUserId = builder.ownerUserId;
        this.media = builder.media;
        this.fileMetadata = builder.fileMetadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<MediaUploadEvent> {
        private long byAdminId;
        private long byUserId;
        private String uploadFrom;
        private long ownerAdminId;
        private long ownerUserId;
        private MediaModel media;
        private FileMetadata fileMetadata;

        public Builder byAdminId(long byAdminId) {
            this.byAdminId = byAdminId;
            return this;
        }

        public Builder byUserId(long byUserId) {
            this.byUserId = byUserId;
            return this;
        }

        public Builder uploadFrom(String uploadFrom) {
            this.uploadFrom = uploadFrom;
            return this;
        }

        public Builder ownerAdminId(long ownerAdminId) {
            this.ownerAdminId = ownerAdminId;
            return this;
        }

        public Builder ownerUserId(long ownerUserId) {
            this.ownerUserId = ownerUserId;
            return this;
        }

        public Builder media(MediaModel media) {
            this.media = media;
            return this;
        }

        public Builder fileMetadata(FileMetadata fileMetadata) {
            this.fileMetadata = fileMetadata;
            return this;
        }

        @Override
        public MediaUploadEvent build() {
            return new MediaUploadEvent(this);
        }
    }
}
