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

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_NOTIFICATION_RECEIVER;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_NOTIFICATION_RECEIVER)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationReceiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "notification_id")
    private long notificationId;

    @Column(name = "to_admin_id")
    private long toAdminId;

    @Column(name = "to_user_id")
    private long toUserId;

    @Column(name = "confidence_level")
    private String confidenceLevel;

    @Column(name = "important_level")
    private String importantLevel;

    private String status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
