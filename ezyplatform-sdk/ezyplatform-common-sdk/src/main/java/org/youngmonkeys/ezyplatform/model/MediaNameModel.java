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

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

@Getter
@Builder
public class MediaNameModel {
    private long id;
    private String name;
    private String originalName;
    private String url;

    public static MediaNameModel fromMediaModel(
        MediaModel model
    ) {
        if (model == null) {
            return null;
        }
        return MediaNameModel.builder()
            .id(model.getId())
            .name(model.getName())
            .originalName(model.getOriginalName())
            .url(model.getUrl())
            .build();
    }

    public String getUrlOrDefault(String defaultUrl) {
        return isBlank(url)
            ? isBlank(name) ? defaultUrl : "/api/v1/media/" + name
            : url;
    }
}
