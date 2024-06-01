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

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_USER_KEYWORD;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_USER_KEYWORD)
@AllArgsConstructor
@NoArgsConstructor
public class UserKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    private String keyword;

    private int priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserKeywordUniqueKey uniqueKey() {
        return new UserKeywordUniqueKey(userId, keyword);
    }
}
