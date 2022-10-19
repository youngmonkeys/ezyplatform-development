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

import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;

public interface UserService {

    UserModel getUserById(long userId);

    UserModel getUserByUsername(String username);

    UserModel getUserByEmail(String mail);

    UserModel getUserByPhone(String phone);

    UserModel getUserByAccessToken(String accessToken);

    List<UserModel> getUserListByIds(
        Collection<Long> userIds
    );

    Long getUserIdByAccessToken(String accessToken);

    long validateAccessToken(String accessToken);

    UserNameModel getUsernameById(long userId);

    Map<Long, UserNameModel> getUsernameMapByIds(
        Collection<Long> userIds
    );

    default Map<Long, UserModel> getUserMapByIds(
        Collection<Long> userIds
    ) {
        return newHashMap(
            getUserListByIds(userIds),
            UserModel::getId
        );
    }

    boolean containsUserById(long id);
}
