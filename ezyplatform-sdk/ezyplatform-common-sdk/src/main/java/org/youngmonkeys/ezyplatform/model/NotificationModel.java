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

@Builder
@Getter
public class NotificationModel {
    private long id;
    private String type;
    private String title;
    private String content;
    private String iconImage;
    private String deepLink;
    private long fromAdminId;
    private long fromUserId;
    private String status;
    private long toAdminId;
    private long toUserId;
    private String confidenceLevel;
    private String importantLevel;
    private String receiveStatus;
    private long sentAt;
    private long receivedAt;
    private long readAt;
}
