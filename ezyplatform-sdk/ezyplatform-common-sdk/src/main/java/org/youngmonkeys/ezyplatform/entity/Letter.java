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

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_LETTER;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_LETTER)
@AllArgsConstructor
@NoArgsConstructor
public class Letter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "letter_type")
    private String type;

    private String title;

    private String content;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "from_admin_id")
    private long fromAdminId;

    @Column(name = "from_user_id")
    private long fromUserId;

    @Column(name = "parent_id")
    private long parentId;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
