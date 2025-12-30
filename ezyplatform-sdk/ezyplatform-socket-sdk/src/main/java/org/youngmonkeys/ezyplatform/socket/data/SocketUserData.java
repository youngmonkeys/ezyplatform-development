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

package org.youngmonkeys.ezyplatform.socket.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.constant.SocketUserType;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@Getter
public class SocketUserData {
    private final long userId;
    private final long adminId;
    private final String userUuid;
    private final String adminUuid;
    private final String userAccessToken;
    private final String adminAccessToken;
    private final String socketUserType;

    protected SocketUserData(Builder builder) {
        this.userId = builder.userId;
        this.adminId = builder.adminId;
        this.userUuid = builder.userUuid;
        this.adminUuid = builder.adminUuid;
        this.userAccessToken = builder.userAccessToken;
        this.adminAccessToken = builder.adminAccessToken;
        this.socketUserType = builder.socketUserType;
    }

    public boolean isAdmin() {
        return SocketUserType.ADMIN.equalsValue(socketUserType);
    }

    public boolean isAnonymous() {
        return SocketUserType.ANONYMOUS.equalsValue(socketUserType);
    }

    public boolean isUser() {
        return SocketUserType.USER.equalsValue(socketUserType);
    }

    public long getSocketUserId() {
        return adminId > ZERO_LONG ? adminId : userId;
    }

    public String getSocketUserUuid() {
        return isNotBlank(adminUuid) ? adminUuid : userUuid;
    }

    public String getSocketUserUuidIncludeType() {
        return socketUserType + "#" + getSocketUserUuid();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<SocketUserData> {
        protected long userId;
        protected long adminId;
        protected String userUuid;
        protected String adminUuid;
        protected String userAccessToken;
        protected String adminAccessToken;
        protected String socketUserType;

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder adminId(long adminId) {
            this.adminId = adminId;
            return this;
        }

        public Builder userUuid(String userUuid) {
            this.userUuid = userUuid;
            return this;
        }

        public Builder adminUuid(String adminUuid) {
            this.adminUuid = adminUuid;
            return this;
        }

        public Builder userAccessToken(String userAccessToken) {
            this.userAccessToken = userAccessToken;
            return this;
        }

        public Builder adminAccessToken(String adminAccessToken) {
            this.adminAccessToken = adminAccessToken;
            return this;
        }

        public Builder socketUserType(String socketUserType) {
            this.socketUserType = socketUserType;
            return this;
        }

        @Override
        public SocketUserData build() {
            return new SocketUserData(this);
        }
    }
}
