/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.data;

import lombok.Builder;
import lombok.Getter;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@Getter
@Builder
public class MediaFileSizeReductionResult {
    private boolean reduced;
    private String originalSizeFileName;
    private String newFileName;
    private String newFileMimeType;
    private long newFileSize;

    public static MediaFileSizeReductionResult NO =
        MediaFileSizeReductionResult.builder().build();

    public String getNewFileMimeTypeOrDefault(
        String defaultValue
    ) {
        return isBlank(newFileMimeType)
            ? defaultValue
            : newFileMimeType;
    }

    public String getNewFileNameOrDefault(
        String defaultValue
    ) {
        return isBlank(newFileName)
            ? defaultValue
            : newFileName;
    }

    public long getNewFileSizeOrDefault(
        long defaultValue
    ) {
        return newFileSize <= ZERO_LONG
            ? defaultValue
            : newFileSize;
    }
}
