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

import lombok.Builder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.constant.ISocketUserType;
import org.youngmonkeys.ezyplatform.constant.SocketUserType;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

@Getter
@Builder
public class SocketUserData {
    private long userId;
    private long adminId;
    private String userUuid;
    private String adminUuid;
    private String userAccessToken;
    private String adminAccessToken;
    private ISocketUserType socketUserType;

    public boolean isAdmin() {
        return socketUserType == SocketUserType.ADMIN;
    }

    public boolean isAnonymous() {
        return socketUserType == SocketUserType.ANONYMOUS;
    }

    public boolean isUser() {
        return socketUserType == SocketUserType.USER;
    }

    public long getSocketUserId() {
        return adminId > 0 ? adminId : userId;
    }

    public String getSocketUserUuid() {
        return isNotBlank(adminUuid) ? adminUuid : userUuid;
    }

    public String getSocketUserUuidIncludeType() {
        return socketUserType + "#" + getSocketUserUuid();
    }

    public SocketUserType getSocketUserTypeCast() {
        return (SocketUserType) socketUserType;
    }
}
