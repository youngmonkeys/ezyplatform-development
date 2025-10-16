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

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public class MediaUpDownloaderManager {

    private final EzyLazyInitializer<Map<String, MediaUpDownloader>>
        mediaUpDownloaderByName;

    @SuppressWarnings("unchecked")
    public MediaUpDownloaderManager(
        EzySingletonFactory singletonFactory
    ) {
        this.mediaUpDownloaderByName = new EzyLazyInitializer<>(() -> {
            List<MediaUpDownloader> uploaders = singletonFactory
                .getSingletonsOf(MediaUpDownloader.class);
            return newHashMap(
                uploaders,
                MediaUpDownloader::getName
            );
        });
    }

    public MediaUpDownloader getMediaUpDownloaderByName(String name) {
        return isBlank(name)
            ? null
            : mediaUpDownloaderByName.get().get(name);
    }

    public List<String> getMediaUpDownloadNames() {
        return new ArrayList<>(
            mediaUpDownloaderByName.get().keySet()
        );
    }

    public List<String> getSortedMediaUpDownloadNames() {
        return getMediaUpDownloadNames()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
