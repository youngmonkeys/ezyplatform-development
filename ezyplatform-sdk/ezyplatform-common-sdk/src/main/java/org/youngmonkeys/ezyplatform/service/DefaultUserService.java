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
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.exception.UserAccessTokenExpiredException;
import org.youngmonkeys.ezyplatform.exception.UserInvalidAccessTokenException;
import org.youngmonkeys.ezyplatform.exception.UserWaiting2FaAccessTokenException;
import org.youngmonkeys.ezyplatform.model.UserAccessTokenModel;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.model.UuidNameModel;
import org.youngmonkeys.ezyplatform.repo.UserAccessTokenRepository;
import org.youngmonkeys.ezyplatform.repo.UserRepository;
import org.youngmonkeys.ezyplatform.result.*;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultUserService implements UserService {

    private final ClockProxy clock;
    private final UserAccessTokenService accessTokenService;
    private final UserRepository userRepository;
    private final UserAccessTokenRepository accessTokenRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultResultToModelConverter resultToModelConverter;

    public List<String> getAllUserStatuses() {
        return newArrayList(
            userRepository.findAllUserStatuses(),
            StatusResult::getStatus
        );
    }

    @Override
    public UserModel getUserById(long id) {
        return userRepository.findByIdOptional(id)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public UserModel getUserByUuid(String uuid) {
        return userRepository.findByFieldOptional("uuid", uuid)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public UserModel getUserByUsername(String username) {
        return userRepository.findByFieldOptional("username", username)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public UserModel getUserByEmail(String email) {
        return userRepository.findByFieldOptional("email", email)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        return userRepository.findByFieldOptional("phone", phone)
            .map(entityToModelConverter::toModel)
            .orElse(null);
    }

    @Override
    public UserModel getUserByAccessToken(String accessToken) {
        long userId = validateUserAccessToken(accessToken);
        return getUserById(userId);
    }

    @Override
    public List<UserModel> getUserListByIds(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            userRepository.findListByIds(userIds),
            entityToModelConverter::toModel
        );
    }

    @Override
    public UserNameModel getUsernameById(long userId) {
        return resultToModelConverter.toUserNameModel(
            userRepository.findUserIdAndNameById(userId)
        );
    }

    public Map<Long, UserNameModel> getUsernameMapByIds(
        Collection<Long> userIds
    ) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<IdNameResult> results = userRepository
            .findUserIdAndNameByIds(userIds);
        return results.stream()
            .collect(
                Collectors.toMap(
                    IdNameResult::getId,
                    resultToModelConverter::toUserNameModel
                )
            );
    }

    @Override
    public UuidNameModel getUserUuidNameById(long userId) {
        return resultToModelConverter.toModel(
            userRepository.findUserUuidNameById(userId)
        );
    }

    @Override
    public Map<Long, UuidNameModel> getUserUuidNameMapByIds(
        Collection<Long> userIds
    ) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findUserUuidNamesByIds(userIds)
            .stream()
            .collect(
                Collectors.toMap(
                    IdUuidNameResult::getId,
                    resultToModelConverter::toModel
                )
            );
    }

    @Override
    public UuidNameModel getUserUuidNameByUuid(String uuid) {
        return resultToModelConverter.toModel(
            userRepository.findUserUuidNameByUuid(uuid)
        );
    }

    @Override
    public Long getUserIdByUuid(String uuid) {
        IdResult result = userRepository.findUserIdByUuid(uuid);
        return result != null ? result.getId() : null;
    }

    @Override
    public Map<String, Long> getUserIdMapByUuids(
        Collection<String> uuids
    ) {
        if (uuids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findUserIdsByUuids(uuids)
            .stream()
            .collect(
                Collectors.toMap(
                    IdUuidResult::getUuid,
                    IdUuidResult::getId
                )
            );
    }

    @Override
    public Long getUserIdByAccessToken(String accessToken) {
        if (accessToken != null) {
            UserAccessToken entity =
                accessTokenRepository.findById(accessToken);
            if (entity != null) {
                return entity.getUserId();
            }
        }
        return null;
    }

    @Override
    public long validateUserAccessToken(String accessToken) {
        return getAccessTokenEntityOrThrowByAccessToken(
            accessToken,
            Boolean.TRUE
        ).getUserId();
    }

    @Override
    public UserAccessTokenModel getUserAccessTokenOrThrowByAccessToken(
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

    protected UserAccessToken getAccessTokenEntityOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    ) {
        if (accessToken == null) {
            throw new UserInvalidAccessTokenException(null);
        }
        UserAccessToken entity = accessTokenRepository.findById(
            accessToken
        );
        if (entity == null) {
            throw new UserInvalidAccessTokenException(accessToken);
        }
        AccessTokenStatus status = entity.getStatus();
        if (verifyStatus
            && status != AccessTokenStatus.ACTIVATED
            && status != AccessTokenStatus.ACTIVATED_2FA
        ) {
            if (status == AccessTokenStatus.WAITING_2FA) {
                throw new UserWaiting2FaAccessTokenException(
                    accessToken
                );
            }
            throw new UserInvalidAccessTokenException(accessToken);
        }
        long userId = accessTokenService.extractUserId(accessToken);
        if (userId != entity.getUserId()) {
            throw new UserInvalidAccessTokenException(accessToken);
        }
        LocalDateTime now = clock.nowDateTime();
        if (now.isAfter(entity.getExpiredAt())) {
            throw new UserAccessTokenExpiredException(accessToken);
        }
        return entity;
    }

    @Override
    public boolean containsUserById(long id) {
        return userRepository.containsById(id);
    }

    @Override
    public long countUsersByStatus(String status) {
        return userRepository.countByStatus(status);
    }
}
