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

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.math.BigDecimal;

@Getter
public class AddMediaModel {
    private final long ownerAdminId;
    private final long ownerUserId;
    private final String url;
    private final String fileName;
    private final String originalFileName;
    private final String mediaType;
    private final String mimeType;
    private final long fileSize;
    private final boolean notPublic;
    private final boolean saveDuration;
    private final BigDecimal durationInMinutes;

    protected AddMediaModel(Builder builder) {
        this.ownerAdminId = builder.ownerAdminId;
        this.ownerUserId = builder.ownerUserId;
        this.url = builder.url;
        this.fileName = builder.fileName;
        this.originalFileName = builder.originalFileName;
        this.mediaType = builder.mediaType;
        this.mimeType = builder.mimeType;
        this.fileSize = builder.fileSize;
        this.notPublic = builder.notPublic;
        this.saveDuration = builder.saveDuration;
        this.durationInMinutes = builder.durationInMinutes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<AddMediaModel> {
        protected long ownerAdminId;
        protected long ownerUserId;
        protected String url;
        protected String fileName;
        protected String originalFileName;
        protected String mediaType;
        protected String mimeType;
        protected long fileSize;
        protected boolean notPublic;
        protected boolean saveDuration;
        protected BigDecimal durationInMinutes;

        public Builder ownerAdminId(long ownerAdminId) {
            this.ownerAdminId = ownerAdminId;
            return this;
        }

        public Builder ownerUserId(long ownerUserId) {
            this.ownerUserId = ownerUserId;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            return mediaType(mediaType.toString());
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder notPublic(boolean notPublic) {
            this.notPublic = notPublic;
            return this;
        }

        public Builder saveDuration(boolean saveDuration) {
            this.saveDuration = saveDuration;
            return this;
        }

        public Builder durationInMinutes(BigDecimal durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
            return this;
        }

        @Override
        public AddMediaModel build() {
            return new AddMediaModel(this);
        }
    }
}
