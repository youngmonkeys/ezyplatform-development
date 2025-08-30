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

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_CONTENT_TEMPLATE;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_CONTENT_TEMPLATE)
@AllArgsConstructor
@NoArgsConstructor
public class ContentTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "owner_id")
    private long ownerId;

    @Column(name = "template_type")
    private String templateType;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "title_template")
    private String titleTemplate;

    @Column(name = "content_template")
    private String contentTemplate;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "creator_type")
    private String creatorType;

    @Column(name = "creator_id")
    private long creatorId;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
