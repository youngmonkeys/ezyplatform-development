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
import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.io.File;

@Getter
public class MediaFileSizeReductionEvent {
    private final MediaType mediaType;
    private final File mediaFilePath;
    private final long expectedFileSize;

    protected MediaFileSizeReductionEvent(Builder builder) {
        this.mediaType = builder.mediaType;
        this.mediaFilePath = builder.mediaFilePath;
        this.expectedFileSize = builder.expectedFileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<MediaFileSizeReductionEvent> {
        private MediaType mediaType;
        private File mediaFilePath;
        private long expectedFileSize;

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder mediaFilePath(File mediaFilePath) {
            this.mediaFilePath = mediaFilePath;
            return this;
        }

        public Builder expectedFileSize(long expectedFileSize) {
            this.expectedFileSize = expectedFileSize;
            return this;
        }

        @Override
        public MediaFileSizeReductionEvent build() {
            return new MediaFileSizeReductionEvent(this);
        }
    }
}
