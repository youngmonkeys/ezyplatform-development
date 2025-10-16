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

package org.youngmonkeys.ezyplatform.media;

import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import lombok.Builder;
import lombok.Getter;
import org.apache.tika.config.TikaConfig;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
@Builder
public class MediaUploadArguments {
    private TikaConfig tika;
    private FileUploader fileUploader;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UploadFrom uploadFrom;
    private long ownerId;
    private boolean avatar;
    private boolean notPublic;
}
