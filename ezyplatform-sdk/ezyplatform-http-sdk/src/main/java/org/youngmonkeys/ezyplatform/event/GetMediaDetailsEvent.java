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

@Getter
public class GetMediaDetailsEvent {
    private final long byAdminId;
    private final long byUserId;
    private final MediaModel media;

    protected GetMediaDetailsEvent(Builder builder) {
        this.byAdminId = builder.byAdminId;
        this.byUserId = builder.byUserId;
        this.media = builder.media;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GetMediaDetailsEvent> {
        private long byAdminId;
        private long byUserId;
        private MediaModel media;

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

        @Override
        public GetMediaDetailsEvent build() {
            return new GetMediaDetailsEvent(this);
        }
    }
}
