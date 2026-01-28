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

import org.youngmonkeys.ezyplatform.entity.AccessTokenType;
import org.youngmonkeys.ezyplatform.entity.UserStatus;
import org.youngmonkeys.ezyplatform.model.UserAccessTokenModel;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.model.UuidNameModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;

@SuppressWarnings("MethodCount")
public interface UserService {

    UserModel getUserById(long userId);

    UserModel getUserByUuid(String uuid);

    UserModel getUserByUsername(String username);

    UserModel getUserByEmail(String mail);

    UserModel getUserByPhone(String phone);

    default UserModel getUserByAccessToken(
        String accessToken
    ) {
        return getUserByAccessToken(
            accessToken,
            AccessTokenType.ACCESS_TOKEN_SINGLE_SET
        );
    }

    UserModel getUserByAccessToken(
        String accessToken,
        Set<String> tokenTypes
    );

    String getUserStatusById(long userId);

    List<UserModel> getUserListByIds(
        Collection<Long> userIds
    );

    List<UserModel> getUserListByUsernames(
        Collection<String> usernames
    );

    List<UserModel> getUserListByPhones(
        Collection<String> phones
    );

    List<UserModel> getUserListByEmails(
        Collection<String> emails
    );

    List<UserModel> getUserListByUuids(
        Collection<String> uuids
    );

    long getUserIdByUsername(String username);

    long getUserIdByEmail(String email);

    long getUserIdByPhone(String phone);

    Long getUserIdByUuid(String uuid);

    Map<String, Long> getUserIdMapByUuids(
        Collection<String> uuids
    );

    Long getUserIdByAccessToken(String accessToken);

    default long validateUserAccessToken(
        String accessToken
    ) {
        return validateUserAccessToken(
            accessToken,
            AccessTokenType.ACCESS_TOKEN_SINGLE_SET
        );
    }

    long validateUserAccessToken(
        String accessToken,
        Set<String> tokenTypes
    );

    UserAccessTokenModel getUserAccessTokenByAccessToken(
        String accessToken
    );

    default UserAccessTokenModel getUserAccessTokenOrThrowByAccessToken(
        String accessToken,
        boolean verifyStatus
    ) {
        return getUserAccessTokenOrThrowByAccessToken(
            accessToken,
            AccessTokenType.ACCESS_TOKEN_SINGLE_SET,
            verifyStatus
        );
    }

    UserAccessTokenModel getUserAccessTokenOrThrowByAccessToken(
        String accessToken,
        Set<String> tokenTypes,
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

    Map<String, UserNameModel> getUsernameMapByUsernames(
        Collection<String> userNames
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

    default Map<String, UserModel> getUserMapByUsernames(
        Collection<String> usernames
    ) {
        return getUserListByUsernames(usernames)
            .stream()
            .filter(it -> it.getUsername() != null)
            .collect(
                Collectors.toMap(
                    UserModel::getUsername,
                    it -> it
                )
            );
    }

    default Map<String, UserModel> getUserMapByEmails(
        Collection<String> emails
    ) {
        return getUserListByEmails(emails)
            .stream()
            .filter(it -> it.getEmail() != null)
            .collect(
                Collectors.toMap(
                    UserModel::getEmail,
                    it -> it
                )
            );
    }

    default Map<String, UserModel> getUserMapByPhones(
        Collection<String> phones
    ) {
        return getUserListByPhones(phones)
            .stream()
            .filter(it -> it.getPhone() != null)
            .collect(
                Collectors.toMap(
                    UserModel::getPhone,
                    it -> it
                )
            );
    }

    default Map<String, UserModel> getUserMapByUuids(
        Collection<String> uuids
    ) {
        return getUserListByUuids(uuids)
            .stream()
            .filter(it -> it.getUuid() != null)
            .collect(
                Collectors.toMap(
                    UserModel::getUuid,
                    it -> it
                )
            );
    }

    boolean containsUserById(long id);

    boolean containsUserByUsername(String username);

    long countUsersByStatus(String status);

    List<UserNameModel> simpleSearch(
        String keyword,
        int limit
    );

    List<UserNameModel> simpleSearch(
        Collection<String> keywords,
        int limit
    );

    List<UserNameModel> simpleSearchWithKeywordPrefix(
        String keywordPrefix,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleIds(
        Collection<Long> roleIds,
        String keyword,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleIds(
        Collection<Long> roleIds,
        Collection<String> keywords,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleIdsAndKeywordPrefix(
        Collection<Long> roleIds,
        String keywordKeyword,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleNames(
        Collection<String> roleNames,
        String keyword,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleNames(
        Collection<String> roleNames,
        Collection<String> keywords,
        int limit
    );

    List<UserNameModel> simpleSearchWithRoleNamesAndKeywordPrefix(
        Collection<String> roleNames,
        String keywordPrefix,
        int limit
    );

    default long countActivatedUsers() {
        return countUsersByStatus(
            UserStatus.ACTIVATED.toString()
        );
    }
}
