/*
 * Copyright 2023 youngmonkeys.org
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
import org.youngmonkeys.ezyplatform.model.MediaModel;

import java.io.File;

@Getter
public class MediaReplacedEvent {
    private final long byAdminId;
    private final long byUserId;
    private final MediaModel media;
    private final File mediaFilePath;

    protected MediaReplacedEvent(Builder builder) {
        this.byAdminId = builder.byAdminId;
        this.byUserId = builder.byUserId;
        this.media = builder.media;
        this.mediaFilePath = builder.mediaFilePath;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<MediaReplacedEvent> {
        private long byAdminId;
        private long byUserId;
        private MediaModel media;
        private File mediaFilePath;

        public Builder byAdminId(long byAdminId) {
            this.byAdminId = byAdminId;
            return this;
        }

        public Builder byUserId(long byUserId) {
            this.byUserId = byUserId;
            return this;
        }

        public Builder media(MediaModel media) {
            this.media = media;
            return this;
        }

        public Builder mediaFilePath(File mediaFilePath) {
            this.mediaFilePath = mediaFilePath;
            return this;
        }

        @Override
        public MediaReplacedEvent build() {
            return new MediaReplacedEvent(this);
        }
    }
}
