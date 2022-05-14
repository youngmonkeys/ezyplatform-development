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

package org.youngmonkeys.ezyplatform.converter;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.model.AdminNameModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.result.AdminNameResult;
import org.youngmonkeys.ezyplatform.result.UserNameResult;
import org.youngmonkeys.ezyplatform.util.LocalDateTimes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
public abstract class DefaultResultToModelConverter {

    protected final ZoneId zoneId;

    public AdminNameModel toModel(AdminNameResult result) {
        return AdminNameModel.builder()
            .adminId(result.getAdminId())
            .username(result.getUsername())
            .displayName(result.getDisplayName())
            .build();
    }

    public UserNameModel toModel(UserNameResult result) {
        return UserNameModel.builder()
            .userId(result.getUserId())
            .username(result.getUsername())
            .displayName(result.getDisplayName())
            .build();
    }

    protected long toTimestamp(LocalDateTime localDateTime) {
        return LocalDateTimes.toTimestamp(localDateTime, zoneId);
    }
}
