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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.entity.UserStatus;
import org.youngmonkeys.ezyplatform.model.UserAccessTokenModel;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.model.UuidNameModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;

public interface UserService {

    UserModel getUserById(long userId);

    UserModel getUserByUuid(String uuid);

    UserModel getUserByUsername(String username);

    UserModel getUserByEmail(String mail);

    UserModel getUserByPhone(String phone);

    UserModel getUserByAccessToken(String accessToken);

    List<UserModel> getUserListByIds(
        Collection<Long> userIds
    );

    Long getUserIdByUuid(String uuid);

    Map<String, Long> getUserIdMapByUuids(
        Collection<String> uuids
    );

    Long getUserIdByAccessToken(String accessToken);

    long validateUserAccessToken(String accessToken);

    UserAccessTokenModel getUserAccessTokenOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    );

    default UserAccessTokenModel getUserAccessTokenOrThrowByAccessToken(
        String accessToken
    ) {
        return getUserAccessTokenOrThrowByAccessToken(
            accessToken,
            Boolean.FALSE
        );
    }

    UserNameModel getUsernameById(long userId);

    Map<Long, UserNameModel> getUsernameMapByIds(
        Collection<Long> userIds
    );

    UuidNameModel getUserUuidNameById(long userId);

    Map<Long, UuidNameModel> getUserUuidNameMapByIds(
        Collection<Long> userIds
    );

    UuidNameModel getUserUuidNameByUuid(String uuid);

    default Map<Long, UserModel> getUserMapByIds(
        Collection<Long> userIds
    ) {
        return newHashMap(
            getUserListByIds(userIds),
            UserModel::getId
        );
    }

    boolean containsUserById(long id);

    long countUsersByStatus(String status);

    default long countActivatedUsers() {
        return countUsersByStatus(
            UserStatus.ACTIVATED.toString()
        );
    }
}
