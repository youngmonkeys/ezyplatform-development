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

import org.youngmonkeys.ezyplatform.model.AdminModel;
import org.youngmonkeys.ezyplatform.model.AdminNameModel;
import org.youngmonkeys.ezyplatform.model.UuidNameModel;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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

    UuidNameModel getAdminUuidNameByUuid(String uuid);

    Long getAdminIdByUuid(String uuid);

    AdminModel getAdminByAccessToken(String accessToken);

    Long getAdminIdByAccessToken(String accessToken);

    long validateAccessToken(String accessToken);
}
