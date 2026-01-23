/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.event;

import lombok.Builder;
import lombok.Getter;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.model.MediaModel;

@Getter
@Builder
public class MediaUploadEvent {
    private String uploadFrom;
    private long ownerAdminId;
    private long ownerUserId;
    private MediaModel media;
    private FileMetadata fileMetadata;
}
