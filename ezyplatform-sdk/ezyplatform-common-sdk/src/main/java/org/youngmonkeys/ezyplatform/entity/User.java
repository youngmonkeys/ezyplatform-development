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

package org.youngmonkeys.ezyplatform.entity;

import com.tvd12.ezyfox.io.EzyStrings;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_USER;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_USER)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String uuid;

    private String username;

    @Column(name = "display_name")
    private String displayName;

    private String password;

    private String email;

    private String phone;

    private String url;

    @Column(name = "avatar_image_id")
    private long avatarImageId;

    @Column(name = "cover_image_id")
    private long coverImageId;

    private String status;

    @Column(name = "activation_key")
    private String activationKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getName() {
        return EzyStrings.isBlank(displayName) ? username : displayName;
    }
}
