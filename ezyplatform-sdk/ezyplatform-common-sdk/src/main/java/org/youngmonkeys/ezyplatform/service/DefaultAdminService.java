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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AdminAccessToken;
import org.youngmonkeys.ezyplatform.exception.AdminAccessTokenExpiredException;
import org.youngmonkeys.ezyplatform.exception.AdminInvalidAccessTokenException;
import org.youngmonkeys.ezyplatform.model.AdminModel;
import org.youngmonkeys.ezyplatform.repo.AdminAccessTokenRepository;
import org.youngmonkeys.ezyplatform.repo.AdminRepository;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
public class DefaultAdminService implements AdminService {

    private final ClockProxy clock;
    private final AdminRepository adminRepository;
    private final AdminAccessTokenRepository accessTokenRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    @Override
    public Optional<AdminModel> getAdminByIdOptional(long id) {
        return adminRepository.findByIdOptional(id)
            .map(entityToModelConverter::toModel);
    }

    @Override
    public AdminModel getAdminById(long adminId) {
        return entityToModelConverter.toModel(
            adminRepository.findById(adminId)
        );
    }

    @Override
    public AdminModel getAdminByUsername(String username) {
        return entityToModelConverter.toModel(
            adminRepository.findByField("username", username)
        );
    }

    @Override
    public Optional<AdminModel> getAdminByUsernameOptional(String username) {
        return adminRepository.findByFieldOptional("username", username)
            .map(entityToModelConverter::toModel);
    }

    @Override
    public AdminModel getAdminByAccessToken(String accessToken) {
        long adminId = validateAccessToken(accessToken);
        return getAdminById(adminId);
    }

    @Override
    public Long getAdminIdByAccessToken(String accessToken) {
        if (accessToken != null) {
            AdminAccessToken entity = accessTokenRepository
                .findById(accessToken);
            if (entity != null) {
                return entity.getAdminId();
            }
        }
        return null;
    }

    @Override
    public long validateAccessToken(String accessToken) {
        if (accessToken == null) {
            throw new AdminInvalidAccessTokenException("null");
        }
        AdminAccessToken entity = accessTokenRepository.findById(accessToken);
        if (entity == null) {
            throw new AdminInvalidAccessTokenException(accessToken);
        }
        LocalDateTime now = clock.nowDateTime();
        if (now.isAfter(entity.getExpiredAt())) {
            throw new AdminAccessTokenExpiredException(accessToken);
        }
        return entity.getAdminId();
    }
}
