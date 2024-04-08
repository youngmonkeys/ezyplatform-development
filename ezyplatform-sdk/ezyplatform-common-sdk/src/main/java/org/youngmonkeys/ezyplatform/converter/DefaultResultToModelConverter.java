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
import org.youngmonkeys.ezyplatform.model.AvatarCoverImageIdsModel;
import org.youngmonkeys.ezyplatform.model.UserNameModel;
import org.youngmonkeys.ezyplatform.model.UuidNameModel;
import org.youngmonkeys.ezyplatform.result.AvatarCoverImageIdsResult;
import org.youngmonkeys.ezyplatform.result.IdNameResult;
import org.youngmonkeys.ezyplatform.result.IdUuidNameResult;
import org.youngmonkeys.ezyplatform.util.LocalDateTimes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
public class DefaultResultToModelConverter {

    protected final ZoneId zoneId;

    public UuidNameModel toModel(IdUuidNameResult result) {
        if (result == null) {
            return null;
        }
        return UuidNameModel.builder()
            .uuid(result.getUuid())
            .displayName(result.getDisplayName())
            .build();
    }

    public AvatarCoverImageIdsModel toModel(
        AvatarCoverImageIdsResult result
    ) {
        if (result == null) {
            return null;
        }
        return AvatarCoverImageIdsModel.builder()
            .avatarImageId(result.getAvatarImageId())
            .coverImageId(result.getCoverImageId())
            .build();
    }

    public AdminNameModel toAdminNameModel(IdNameResult result) {
        if (result == null) {
            return null;
        }
        return AdminNameModel.builder()
            .adminId(result.getId())
            .username(result.getUsername())
            .displayName(result.getDisplayName())
            .build();
    }

    public UserNameModel toUserNameModel(IdNameResult result) {
        if (result == null) {
            return null;
        }
        return UserNameModel.builder()
            .userId(result.getId())
            .username(result.getUsername())
            .displayName(result.getDisplayName())
            .build();
    }

    protected long toTimestamp(LocalDateTime localDateTime) {
        return LocalDateTimes.toTimestamp(localDateTime, zoneId);
    }
}
