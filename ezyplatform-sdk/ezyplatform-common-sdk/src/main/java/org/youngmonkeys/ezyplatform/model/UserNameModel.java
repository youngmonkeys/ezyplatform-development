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

import com.tvd12.ezyfox.io.EzyStrings;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserNameModel {
    private long userId;
    private String username;
    private String displayName;

    public static UserNameModel fromUserModel(UserModel model) {
        if (model == null) {
            return null;
        }
        return UserNameModel.builder()
            .userId(model.getId())
            .username(model.getUsername())
            .displayName(model.getDisplayName())
            .build();
    }

    public String getName() {
        return EzyStrings.isBlank(displayName) ? username : displayName;
    }
}
