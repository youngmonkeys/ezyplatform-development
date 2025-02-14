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
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.exception.UserAccessTokenExpiredException;
import org.youngmonkeys.ezyplatform.exception.UserInvalidAccessTokenException;
import org.youngmonkeys.ezyplatform.exception.UserNotFoundException;
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
import static com.tvd12.ezyfox.util.Next.limit;
import static org.youngmonkeys.ezyplatform.util.StringConverters.trimOrNull;

@SuppressWarnings("MethodCount")
@AllArgsConstructor
public class DefaultUserService implements UserService {

    private final ClockProxy clock;
    private final UserAccessTokenService accessTokenService;
    private final UserRepository userRepository;
    private final UserAccessTokenRepository accessTokenRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultResultToModelConverter resultToModelConverter;

    public void updateUserEmail(
        long userId,
        String email
    ) {
        User user = getUserEntityByIdOrThrow(userId);
        user.setEmail(trimOrNull(email));
        user.setUpdatedAt(clock.nowDateTime());
        userRepository.save(user);
    }

    public void updateUserPhone(
        long userId,
        String phone
    ) {
        User user = getUserEntityByIdOrThrow(userId);
        user.setPhone(trimOrNull(phone));
        user.setUpdatedAt(clock.nowDateTime());
        userRepository.save(user);
    }

    public void updateUserDisplayName(
        long userId,
        String displayName
    ) {
        User user = getUserEntityByIdOrThrow(userId);
        user.setDisplayName(trimOrNull(displayName));
        user.setUpdatedAt(clock.nowDateTime());
        userRepository.save(user);
    }

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
    public List<UserModel> getUserListByUsernames(
        Collection<String> usernames
    ) {
        if (usernames.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            userRepository.findByUsernameIn(usernames),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<UserModel> getUserListByPhones(
        Collection<String> phones
    ) {
        if (phones.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            userRepository.findByPhoneIn(phones),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<UserModel> getUserListByEmails(
        Collection<String> emails
    ) {
        if (emails.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            userRepository.findByEmailIn(emails),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<UserModel> getUserListByUuids(
        Collection<String> uuids
    ) {
        if (uuids.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            userRepository.findByUuidIn(uuids),
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
    public Map<String, UserNameModel> getUsernameMapByUsernames(
        Collection<String> userNames
    ) {
        if (userNames.isEmpty()) {
            return Collections.emptyMap();
        }
        List<IdNameResult> results = userRepository
            .findUserIdAndNameByUsernames(userNames);
        return results.stream()
            .collect(
                Collectors.toMap(
                    IdNameResult::getUsername,
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
    public long getUserIdByUsername(String username) {
        IdResult result = userRepository
            .findUserIdByUsername(username);
        return result != null ? result.getId() : 0L;
    }

    @Override
    public long getUserIdByEmail(String email) {
        IdResult result = userRepository.findUserIdByEmail(email);
        return result != null ? result.getId() : 0L;
    }

    @Override
    public long getUserIdByPhone(String phone) {
        IdResult result = userRepository.findUserIdByPhone(phone);
        return result != null ? result.getId() : 0L;
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
    public UserAccessTokenModel getUserAccessTokenByAccessToken(
        String accessToken
    ) {
        return entityToModelConverter.toModel(
            accessTokenRepository.findById(
                accessToken
            )
        );
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
        long userId = accessTokenService.extractUserId(accessToken);
        if (userId <= 0L) {
            throw new UserInvalidAccessTokenException(accessToken);
        }
        UserAccessToken entity = accessTokenRepository.findById(
            accessToken
        );
        if (entity == null) {
            throw new UserInvalidAccessTokenException(accessToken);
        }
        if (userId != entity.getUserId()) {
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
    public boolean containsUserByUsername(String username) {
        return userRepository.containsByField(
            "username",
            username
        );
    }

    @Override
    public List<UserNameModel> simpleSearch(
        String keyword,
        int limit
    ) {
        List<IdNameResult> results = userRepository
            .findUsernameByUniqueKeyword(
                keyword,
                limit(limit)
            );
        if (results.isEmpty()) {
            results = userRepository.findUsernameByKeyword(
                keyword,
                limit(limit)
            );
        }
        return newArrayList(
            results,
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public List<UserNameModel> simpleSearch(
        Collection<String> keywords,
        int limit
    ) {
        List<IdNameResult> results = userRepository
            .findUsernameByUniqueKeywords(
                keywords,
                limit(limit)
            );
        if (results.isEmpty()) {
            results = userRepository.findUsernameByKeywords(
                keywords,
                limit(limit)
            );
        }
        return newArrayList(
            results,
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public List<UserNameModel> simpleSearchWithRoleIds(
        Collection<Long> roleIds,
        String keyword,
        int limit
    ) {
        return newArrayList(
            userRepository.findUsernameByKeywordAndRoleIds(
                roleIds,
                keyword,
                limit(limit)
            ),
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public List<UserNameModel> simpleSearchWithRoleIds(
        Collection<Long> roleIds,
        Collection<String> keywords,
        int limit
    ) {
        return newArrayList(
            userRepository.findUsernameByKeywordsAndRoleIds(
                roleIds,
                keywords,
                limit(limit)
            ),
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public List<UserNameModel> simpleSearchWithRoleNames(
        Collection<String> roleNames,
        String keyword,
        int limit
    ) {
        return newArrayList(
            userRepository.findUsernameByKeywordAndRoleNames(
                roleNames,
                keyword,
                limit(limit)
            ),
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public List<UserNameModel> simpleSearchWithRoleNames(
        Collection<String> roleNames,
        Collection<String> keywords,
        int limit
    ) {
        return newArrayList(
            userRepository.findUsernameByKeywordsAndRoleNames(
                roleNames,
                keywords,
                limit(limit)
            ),
            resultToModelConverter::toUserNameModel
        );
    }

    @Override
    public long countUsersByStatus(String status) {
        return userRepository.countByStatus(status);
    }

    protected User getUserEntityByIdOrThrow(long userId) {
        User entity = userRepository.findById(userId);
        if (entity == null) {
            throw new UserNotFoundException(userId);
        }
        return entity;
    }
}
