/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.manager;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;
import org.youngmonkeys.ezyplatform.fetcher.ViewMultipleSchemaFetcher;
import org.youngmonkeys.ezyplatform.fetcher.ViewSchemaFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewSchemaFetcherManager {

    private final EzyLazyInitializer<Map<String, ViewSchemaFetcher>>
        viewSchemaFetcherByUri;

    @SuppressWarnings("unchecked")
    public ViewSchemaFetcherManager(
        EzySingletonFactory singletonFactory
    ) {
        this.viewSchemaFetcherByUri = new EzyLazyInitializer<>(() -> {
            Map<String, ViewSchemaFetcher> map =
                ((List<ViewSchemaFetcher>) singletonFactory
                    .getSingletonsOf(ViewSchemaFetcher.class)
                )
                .stream()
                .collect(
                    Collectors.toMap(
                        ViewSchemaFetcher::getViewUri,
                        it -> it
                    )
                );
            ((List<ViewMultipleSchemaFetcher>) singletonFactory
                .getSingletonsOf(ViewMultipleSchemaFetcher.class)
            )
                .stream()
                .flatMap(it -> it.getViewSchemaFetchers().stream())
                .forEach(it ->
                    map.put(it.getViewUri(), it)
                );
            return map;
        });
    }

    public ViewSchemaFetcher getViewSchemaByUri(
        String viewUri
    ) {
        return viewSchemaFetcherByUri
            .get()
            .get(viewUri);
    }

    public List<ViewSchemaFetcher> getViewSchemaFetchers() {
        return new ArrayList<>(
            viewSchemaFetcherByUri
                .get()
                .values()
        );
    }
}
