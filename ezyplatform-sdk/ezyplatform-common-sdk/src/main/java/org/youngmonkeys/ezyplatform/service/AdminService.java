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

import org.youngmonkeys.ezyplatform.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;

public interface AdminService {

    AdminModel getAdminById(long adminId);

    Optional<AdminModel> getAdminByIdOptional(long adminId);

    AdminModel getAdminByUuid(String uuid);

    Optional<AdminModel> getAdminByUuidOptional(
        String uuid
    );

    AdminModel getAdminByUsername(String username);
    
    Optional<AdminModel> getAdminByUsernameOptional(
        String username
    );

    AdminNameModel getAdminNameById(long adminId);

    Map<Long, AdminNameModel> getAdminNameMapByIds(
        Collection<Long> adminIds
    );

    UuidNameModel getAdminUuidNameById(long adminId);

    Map<Long, UuidNameModel> getAdminUuidNameMapByIds(
        Collection<Long> adminIds
    );

    AvatarCoverImageIdsModel getAdminAvatarCoverImageIdsById(
        long adminId
    );

    UuidNameModel getAdminUuidNameByUuid(String uuid);

    Long getAdminIdByUuid(String uuid);

    AdminModel getAdminByAccessToken(String accessToken);

    Long getAdminIdByAccessToken(String accessToken);

    default Map<Long, AdminModel> getAdminMapByIds(
        Collection<Long> ids
    ) {
        return newHashMap(
            getAdminsByIds(ids),
            AdminModel::getId
        );
    }

    List<AdminModel> getAdminsByIds(
        Collection<Long> ids
    );

    List<AdminModel> getAdminsByUuids(
        Collection<String> uuids
    );

    Map<String, AdminModel> getAdminMapByUuids(
        Collection<String> uuids
    );

    AdminModel getAdminByEmail(String email);

    AdminModel getAdminByPhone(String phone);

    AdminModel getAdminByUsernameOrEmail(
        String usernameOrEmail
    );

    long validateAdminAccessToken(String accessToken);

    AdminAccessTokenModel getAdminAccessTokenOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    );

    default AdminAccessTokenModel getAdminAccessTokenOrThrowByAccessToken(
        String accessToken
    ) {
        return getAdminAccessTokenOrThrowByAccessToken(
            accessToken,
            Boolean.FALSE
        );
    }
}
