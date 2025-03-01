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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.entity.AccessTokenStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserAccessTokenModel
    implements AccessTokenModel {
    private long userId;
    private String accessToken;
    private long renewalCount;
    private AccessTokenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private long createdAtTimestamp;
    private long expiredAtTimestamp;
}
