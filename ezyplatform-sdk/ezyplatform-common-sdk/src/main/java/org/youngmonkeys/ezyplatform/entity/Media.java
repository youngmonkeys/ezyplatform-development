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

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_MEDIA;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_MEDIA)
@AllArgsConstructor
@NoArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String url;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "upload_from")
    private String uploadFrom;

    @Column(name = "media_type")
    private String type;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "owner_user_id")
    private long ownerUserId;

    @Column(name = "owner_admin_id")
    private long ownerAdminId;

    private String title = EMPTY_STRING;

    private String caption = EMPTY_STRING;

    @Column(name = "alternative_text")
    private String alternativeText = EMPTY_STRING;

    private String description = EMPTY_STRING;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "public_media")
    private boolean publicMedia = true;

    private String status = MediaStatus.ADDED.toString();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
