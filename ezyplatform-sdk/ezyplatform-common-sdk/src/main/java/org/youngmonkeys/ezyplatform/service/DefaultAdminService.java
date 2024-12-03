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
import org.youngmonkeys.ezyplatform.converter.DefaultResultToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AccessTokenStatus;
import org.youngmonkeys.ezyplatform.entity.AdminAccessToken;
import org.youngmonkeys.ezyplatform.exception.AdminAccessTokenExpiredException;
import org.youngmonkeys.ezyplatform.exception.AdminInvalidAccessTokenException;
import org.youngmonkeys.ezyplatform.exception.AdminWaiting2FaAccessTokenException;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.repo.AdminAccessTokenRepository;
import org.youngmonkeys.ezyplatform.repo.AdminRepository;
import org.youngmonkeys.ezyplatform.result.IdNameResult;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.result.IdUuidNameResult;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultAdminService implements AdminService {

    private final ClockProxy clock;
    private final AdminAccessTokenService accessTokenService;
    private final AdminRepository adminRepository;
    private final AdminAccessTokenRepository accessTokenRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultResultToModelConverter resultToModelConverter;

    @Override
    public AdminModel getAdminById(long adminId) {
        return entityToModelConverter.toModel(
            adminRepository.findById(adminId)
        );
    }

    @Override
    public Optional<AdminModel> getAdminByIdOptional(long adminId) {
        return adminRepository.findByIdOptional(adminId)
            .map(entityToModelConverter::toModel);
    }

    @Override
    public AdminModel getAdminByUuid(String uuid) {
        return entityToModelConverter.toModel(
            adminRepository.findByField("uuid", uuid)
        );
    }

    @Override
    public Optional<AdminModel> getAdminByUuidOptional(String uuid) {
        return adminRepository.findByFieldOptional("uuid", uuid)
            .map(entityToModelConverter::toModel);
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
    public AdminNameModel getAdminNameById(long adminId) {
        return resultToModelConverter.toAdminNameModel(
            adminRepository.findAdminNameById(adminId)
        );
    }

    @Override
    public Map<Long, AdminNameModel> getAdminNameMapByIds(
        Collection<Long> adminIds
    ) {
        if (adminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminRepository.findAdminNamesByIds(adminIds)
            .stream()
            .collect(
                Collectors.toMap(
                    IdNameResult::getId,
                    resultToModelConverter::toAdminNameModel
                )
            );
    }

    @Override
    public UuidNameModel getAdminUuidNameById(long adminId) {
        return resultToModelConverter.toModel(
            adminRepository.findAdminUuidNameById(adminId)
        );
    }

    @Override
    public Map<Long, UuidNameModel> getAdminUuidNameMapByIds(
        Collection<Long> adminIds
    ) {
        if (adminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminRepository.findAdminUuidNamesByIds(adminIds)
            .stream()
            .collect(
                Collectors.toMap(
                    IdUuidNameResult::getId,
                    resultToModelConverter::toModel
                )
            );
    }

    @Override
    public AvatarCoverImageIdsModel getAdminAvatarCoverImageIdsById(
        long adminId
    ) {
        return resultToModelConverter.toModel(
            adminRepository.findAdminAvatarCoverImageIdsById(
                adminId
            )
        );
    }

    @Override
    public UuidNameModel getAdminUuidNameByUuid(String uuid) {
        return resultToModelConverter.toModel(
            adminRepository.findAdminUuidNameByUuid(uuid)
        );
    }

    @Override
    public Long getAdminIdByUuid(String uuid) {
        IdResult result = adminRepository.findAdminIdByUuid(uuid);
        return result != null ? result.getId() : null;
    }

    @Override
    public AdminModel getAdminByAccessToken(String accessToken) {
        long adminId = validateAdminAccessToken(accessToken);
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
    public long validateAdminAccessToken(String accessToken) {
        return getAccessTokenEntityOrThrowByAccessToken(
            accessToken,
            Boolean.TRUE
        ).getAdminId();
    }

    @Override
    public AdminAccessTokenModel getAdminAccessTokenOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    ) {
        return entityToModelConverter.toModel(
            getAccessTokenEntityOrThrowByAccessToken(
                accessToken,
                verifyStatus
            )
        );
    }

    protected AdminAccessToken getAccessTokenEntityOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    ) {
        if (accessToken == null) {
            throw new AdminInvalidAccessTokenException("null");
        }
        long adminId = accessTokenService.extractAdminId(accessToken);
        if (adminId <= 0L) {
            throw new AdminInvalidAccessTokenException(accessToken);
        }
        AdminAccessToken entity = accessTokenRepository.findById(
            accessToken
        );
        if (entity == null) {
            throw new AdminInvalidAccessTokenException(accessToken);
        }
        if (adminId != entity.getAdminId()) {
            throw new AdminInvalidAccessTokenException(accessToken);
        }
        AccessTokenStatus status = entity.getStatus();
        if (verifyStatus
            && status != AccessTokenStatus.ACTIVATED
            && status != AccessTokenStatus.ACTIVATED_2FA
        ) {
            if (status == AccessTokenStatus.WAITING_2FA) {
                throw new AdminWaiting2FaAccessTokenException(
                    accessToken
                );
            }
            throw new AdminInvalidAccessTokenException(accessToken);
        }
        LocalDateTime now = clock.nowDateTime();
        if (now.isAfter(entity.getExpiredAt())) {
            throw new AdminAccessTokenExpiredException(accessToken);
        }
        return entity;
    }
}
