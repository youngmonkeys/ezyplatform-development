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
import org.youngmonkeys.ezyplatform.entity.AdminStatus;

@Getter
@Builder
public class AdminModel {
    private long id;
    private String username;
    private String password;
    private String email;
    private String displayName;
    private String phone;
    private String url;
    private long avatarImageId;
    private long coverImageId;
    private AdminStatus status;
    private long createdAt;
    private long updatedAt;

    public String getName() {
        return EzyStrings.isBlank(displayName) ? username : displayName;
    }
}
