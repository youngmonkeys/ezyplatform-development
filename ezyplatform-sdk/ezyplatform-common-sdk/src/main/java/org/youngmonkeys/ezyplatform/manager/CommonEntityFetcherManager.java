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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzySets.combine;
import static com.tvd12.ezyfox.io.EzySets.newHashSet;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.model.CommonEntityModel.defaultEntity;

public class CommonEntityFetcherManager {

    private final EzyLazyInitializer<Map<String, CommonEntityFetcher>>
        accountingEntityFetcherByEntityType;
    private final EzyLazyInitializer<Map<String, Map<String, CommonEntityFetcher>>>
        accountingEntityFetcherByEntityTypeByModuleName;
    private final Map<String, CommonEntityFetcher> additionalFetcherByEntityType;
    private final Map<String, Map<String, CommonEntityFetcher>>
        additionalFetcherByEntityTypeByModuleName;

    @SuppressWarnings("unchecked")
    public CommonEntityFetcherManager(
        EzySingletonFactory singletonFactory
    ) {
        this.accountingEntityFetcherByEntityType = new EzyLazyInitializer<>(() -> {
            List<CommonEntityFetcher> beans = singletonFactory
                .getSingletonsOf(CommonEntityFetcher.class);
            Map<String, CommonEntityFetcher> map = new ConcurrentHashMap<>();
            for (CommonEntityFetcher fetcher : beans) {
                if (isNotBlank(fetcher.getModuleName())) {
                    continue;
                }
                for (String entityType : fetcher.getEntityTypes()) {
                    map.compute(
                        entityType,
                        (k, v) -> v == null || fetcher.getPriority() >= v.getPriority()
                            ? fetcher
                            : v
                    );
                }
            }
            List<CommonEntityFetcher> prioritizedBeans =
                getPrioritizedEntityFetchers(singletonFactory);
            for (CommonEntityFetcher fetcher : prioritizedBeans) {
                if (isNotBlank(fetcher.getModuleName())) {
                    continue;
                }
                for (String entityType : fetcher.getEntityTypes()) {
                    map.put(entityType, fetcher);
                }
            }
            return map;
        });
        this.accountingEntityFetcherByEntityTypeByModuleName = new EzyLazyInitializer<>(() -> {
            List<CommonEntityFetcher> beans = singletonFactory
                .getSingletonsOf(CommonEntityFetcher.class);
            Map<String, Map<String, CommonEntityFetcher>> mapByModuleName =
                new ConcurrentHashMap<>();
            for (CommonEntityFetcher fetcher : beans) {
                String moduleName = fetcher.getModuleName();
                if (isBlank(moduleName)) {
                    continue;
                }
                for (String entityType : fetcher.getEntityTypes()) {
                    mapByModuleName
                        .computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>())
                        .compute(
                            entityType,
                            (k, v) -> v == null || fetcher.getPriority() >= v.getPriority()
                                ? fetcher
                                : v
                        );
                }
            }
            List<CommonEntityFetcher> prioritizedBeans =
                getPrioritizedEntityFetchers(singletonFactory);
            for (CommonEntityFetcher fetcher : prioritizedBeans) {
                String moduleName = fetcher.getModuleName();
                if (isBlank(moduleName)) {
                    continue;
                }
                for (String entityType : fetcher.getEntityTypes()) {
                    mapByModuleName
                        .computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>())
                        .put(entityType, fetcher);
                }
            }
            return mapByModuleName;
        });
        this.additionalFetcherByEntityType = new ConcurrentHashMap<>();
        this.additionalFetcherByEntityTypeByModuleName = new ConcurrentHashMap<>();
    }

    protected List<CommonEntityFetcher> getPrioritizedEntityFetchers(
        EzySingletonFactory singletonFactory
    ) {
        return Collections.emptyList();
    }

    public void addEntityFetcher(CommonEntityFetcher fetcher) {
        String moduleName = fetcher.getModuleName();
        if (isBlank(moduleName)) {
            for (String entityType : fetcher.getEntityTypes()) {
                additionalFetcherByEntityType.put(entityType, fetcher);
            }
        } else {
            for (String entityType : fetcher.getEntityTypes()) {
                additionalFetcherByEntityTypeByModuleName
                    .computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>())
                    .put(entityType, fetcher);
            }
        }
    }

    public CommonEntityFetcher getEntityFetcherByModuleNameAndEntityType(
        String moduleName,
        String entityType
    ) {
        CommonEntityFetcher fetcher = additionalFetcherByEntityTypeByModuleName
            .getOrDefault(moduleName, Collections.emptyMap())
            .get(entityType);
        if (fetcher == null) {
            fetcher = accountingEntityFetcherByEntityTypeByModuleName
                .get()
                .getOrDefault(moduleName, Collections.emptyMap())
                .get(entityType);
        }
        if (fetcher == null) {
            fetcher = getEntityFetcherByEntityType(entityType);
        }
        return fetcher;
    }

    public CommonEntityFetcher getEntityFetcherByEntityType(
        String entityType
    ) {
        CommonEntityFetcher fetcher = additionalFetcherByEntityType
            .get(entityType);
        if (fetcher == null) {
            fetcher = accountingEntityFetcherByEntityType
                .get()
                .get(entityType);
        }
        return fetcher;
    }

    public CommonEntityModel getEntityByModuleNameAndEntityTypeAndId(
        String moduleName,
        String entityType,
        long entityId
    ) {
        CommonEntityFetcher fetcher = getEntityFetcherByModuleNameAndEntityType(
            moduleName,
            entityType
        );
        return fetcher != null
            ? fetcher.getEntityById(entityId)
            : defaultEntity(entityId, entityType);
    }

    public CommonEntityModel getEntityByEntityTypeAndId(
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

    public Map<Long, CommonEntityModel> getEntityMapByModuleNameAndEntityTypeAndIds(
        String moduleName,
        String entityType,
        Collection<Long> entityIds
    ) {
        CommonEntityFetcher fetcher = getEntityFetcherByModuleNameAndEntityType(
            moduleName,
            entityType
        );
        return fetcher != null
            ? fetcher.getEntityMapByIds(entityIds)
            : Collections.emptyMap();
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

    public Map<String, Map<Long, CommonEntityModel>> getEntityMapByModuleNameAndEntityIdsByType(
        String moduleName,
        Map<String, Set<Long>> entityIdsByType
    ) {
        return entityIdsByType
            .entrySet()
            .parallelStream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> getEntityMapByModuleNameAndEntityTypeAndIds(
                        moduleName,
                        e.getKey(),
                        e.getValue()
                    )
                )
            );
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

    @SuppressWarnings("unchecked")
    public Set<String> getAllEntityTypes() {
        return combine(
            accountingEntityFetcherByEntityType.get().keySet(),
            additionalFetcherByEntityType.keySet(),
            accountingEntityFetcherByEntityTypeByModuleName
                .get()
                .values()
                .stream()
                .flatMap(it -> it.keySet().stream())
                .collect(Collectors.toSet()),
            additionalFetcherByEntityTypeByModuleName
                .values()
                .stream()
                .flatMap(it -> it.keySet().stream())
                .collect(Collectors.toSet())
        );
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAllModelNames() {
        return combine(
            newHashSet(
                accountingEntityFetcherByEntityType.get().values(),
                CommonEntityFetcher::getModelName
            ),
            newHashSet(
                additionalFetcherByEntityType.values(),
                CommonEntityFetcher::getModelName
            ),
            accountingEntityFetcherByEntityTypeByModuleName
                .get()
                .values()
                .stream()
                .flatMap(it -> it.values()
                    .stream()
                    .map(CommonEntityFetcher::getModelName)
                )
                .collect(Collectors.toSet()),
            additionalFetcherByEntityTypeByModuleName
                .values()
                .stream()
                .flatMap(it ->
                    it.values()
                        .stream()
                        .map(CommonEntityFetcher::getModelName)
                )
                .collect(Collectors.toSet())
        );
    }
}
