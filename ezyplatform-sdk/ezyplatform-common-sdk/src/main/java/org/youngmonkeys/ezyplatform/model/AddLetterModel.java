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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class AddLetterModel {
    private final String type;
    private final String title;
    private final String content;
    private final long fromAdminId;
    private final long fromUserId;
    private final String letterStatus;
    private final Set<Long> toAdminIds;
    private final Set<Long> toUserIds;
    private final String confidenceLevel;
    private final String importantLevel;
    private final String sendStatus;
    private final long notificationReceiverId;

    protected AddLetterModel(Builder builder) {
        this.type = builder.type;
        this.title = builder.title;
        this.content = builder.content;
        this.fromAdminId = builder.fromAdminId;
        this.fromUserId = builder.fromUserId;
        this.letterStatus = builder.letterStatus;
        this.toAdminIds = builder.toAdminIds;
        this.toUserIds = builder.toUserIds;
        this.confidenceLevel = builder.confidenceLevel;
        this.importantLevel = builder.importantLevel;
        this.sendStatus = builder.sendStatus;
        this.notificationReceiverId = builder.notificationReceiverId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<AddLetterModel> {
        protected String type;
        protected String title;
        protected String content;
        protected long fromAdminId;
        protected long fromUserId;
        protected String letterStatus;
        protected String confidenceLevel;
        protected String importantLevel;
        protected String sendStatus;
        private long notificationReceiverId;
        protected Set<Long> toAdminIds = new HashSet<>();
        protected Set<Long> toUserIds = new HashSet<>();

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder fromAdminId(long fromAdminId) {
            this.fromAdminId = fromAdminId;
            return this;
        }

        public Builder fromUserId(long fromUserId) {
            this.fromUserId = fromUserId;
            return this;
        }


        public Builder letterStatus(String letterStatus) {
            this.letterStatus = letterStatus;
            return this;
        }

        public Builder toAdminId(long toAdminId) {
            this.toAdminIds.add(toAdminId);
            return this;
        }

        public Builder toAdminIds(Collection<Long> toAdminIds) {
            this.toAdminIds.addAll(toAdminIds);
            return this;
        }

        public Builder toUserId(long toUserId) {
            this.toUserIds.add(toUserId);
            return this;
        }

        public Builder toUserIds(Collection<Long> toUserIds) {
            this.toUserIds.addAll(toUserIds);
            return this;
        }

        public Builder confidenceLevel(String confidenceLevel) {
            this.confidenceLevel = confidenceLevel;
            return this;
        }

        public Builder importantLevel(String importantLevel) {
            this.importantLevel = importantLevel;
            return this;
        }

        public Builder sendStatus(String sendStatus) {
            this.sendStatus = sendStatus;
            return this;
        }

        public Builder notificationReceiverId(long notificationReceiverId) {
            this.notificationReceiverId = notificationReceiverId;
            return this;
        }

        @Override
        public AddLetterModel build() {
            return new AddLetterModel(this);
        }
    }
}
