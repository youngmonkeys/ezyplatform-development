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

package org.youngmonkeys.ezyplatform.manager;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;
import org.youngmonkeys.ezyplatform.fetcher.CommonEntityFetcher;
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;

import java.util.*;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

public class CommonEntityFetcherManager {

    private final EzyLazyInitializer<Map<String, CommonEntityFetcher>>
        accountingEntityFetcherByEntityType;
    private final Map<String, CommonEntityFetcher> additionalFetcherByEntityType;

    @SuppressWarnings("unchecked")
    public CommonEntityFetcherManager(
        EzySingletonFactory singletonFactory
    ) {
        this.accountingEntityFetcherByEntityType = new EzyLazyInitializer<>(() -> {
            List<CommonEntityFetcher> beans = singletonFactory
                .getSingletonsOf(CommonEntityFetcher.class);
            Map<String, CommonEntityFetcher> map = new HashMap<>();
            for (CommonEntityFetcher fetcher : beans) {
                map.compute(
                    fetcher.getEntityType(),
                    (k, v) -> v == null || fetcher.getPriority() >= v.getPriority()
                        ? fetcher
                        : v
                );
            }
            List<CommonEntityFetcher> prioritizedBeans =
                getPrioritizedEntityFetchers(singletonFactory);
            for (CommonEntityFetcher fetcher : prioritizedBeans) {
                map.put(fetcher.getEntityType(), fetcher);
            }
            return map;
        });
        this.additionalFetcherByEntityType = new HashMap<>();
    }

    protected List<CommonEntityFetcher> getPrioritizedEntityFetchers(
        EzySingletonFactory singletonFactory
    ) {
        return Collections.emptyList();
    }

    public void addEntityFetcher(CommonEntityFetcher fetcher) {
        additionalFetcherByEntityType.put(
            fetcher.getEntityType(),
            fetcher
        );
    }

    public CommonEntityFetcher getEntityFetcherByEntityType(
        String entityType
    ) {
        return additionalFetcherByEntityType.getOrDefault(
            entityType,
            accountingEntityFetcherByEntityType
                .get()
                .get(entityType)
        );
    }

    public CommonEntityModel getEntityEntityTypeAndId(
        String entityType,
        long entityId
    ) {
        CommonEntityFetcher fetcher = getEntityFetcherByEntityType(
            entityType
        );
        return fetcher != null
            ? fetcher.getEntityById(entityId)
            : defaultEntity(entityId, entityType);
    }

    public Map<Long, CommonEntityModel> getEntityMapByEntityTypeAndIds(
        String entityType,
        Collection<Long> entityIds
    ) {
        CommonEntityFetcher fetcher = getEntityFetcherByEntityType(
            entityType
        );
        return fetcher != null
            ? fetcher.getEntityMapByIds(entityIds)
            : Collections.emptyMap();
    }

    public Map<String, Map<Long, CommonEntityModel>> getEntityMapByEntityIdsByType(
        Map<String, Set<Long>> entityIdsByType
    ) {
        return entityIdsByType
            .entrySet()
            .parallelStream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> getEntityMapByEntityTypeAndIds(
                        e.getKey(),
                        e.getValue()
                    )
                )
            );
    }
}
