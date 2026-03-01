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

package org.youngmonkeys.ezyplatform.test.manager;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.fetcher.CommonEntityFetcher;
import org.youngmonkeys.ezyplatform.manager.CommonEntityFetcherManager;
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonEntityFetcherManagerTest {

    @Test
    public void shouldChooseHighestPriorityFetcherFromFactory() {
        // given
        CommonEntityModel lowModel = CommonEntityModel.builder()
            .id(1L)
            .name("low-user")
            .displayName("Low user")
            .build();
        CommonEntityModel highModel = CommonEntityModel.builder()
            .id(2L)
            .name("high-user")
            .displayName("High user")
            .build();

        CommonEntityFetcher lowPriorityFetcher = new TestCommonEntityFetcher(
            "user",
            1,
            Collections.singletonMap(lowModel.getId(), lowModel),
            "user-low-model"
        );
        CommonEntityFetcher highPriorityFetcher = new TestCommonEntityFetcher(
            "user",
            2,
            Collections.singletonMap(highModel.getId(), highModel),
            "user-high-model"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(Arrays.asList(lowPriorityFetcher, highPriorityFetcher));

        CommonEntityFetcherManager manager = new CommonEntityFetcherManager(
            singletonFactory
        );

        // when
        CommonEntityFetcher actualFetcher = manager.getEntityFetcherByEntityType(
            "user"
        );
        CommonEntityModel actualModel = manager.getEntityByEntityTypeAndId(
            "user",
            highModel.getId()
        );
        Map<Long, CommonEntityModel> entityMap =
            manager.getEntityMapByEntityTypeAndIds(
                "user",
                Collections.singleton(highModel.getId())
            );

        // then
        Asserts.assertEquals(actualFetcher, highPriorityFetcher);
        Asserts.assertEquals(actualModel.getDisplayName(), highModel.getDisplayName());
        Asserts.assertEquals(entityMap.size(), 1);
        Asserts.assertEquals(entityMap.get(highModel.getId()), highModel);
    }

    @Test
    public void shouldSupportPrioritizedAndAdditionalFetchers() {
        // given
        CommonEntityModel baseModel = CommonEntityModel.builder()
            .id(1L)
            .name("base-user")
            .displayName("Base user")
            .build();
        CommonEntityModel prioritizedModel = CommonEntityModel.builder()
            .id(3L)
            .name("prioritized-user")
            .displayName("Prioritized user")
            .build();
        CommonEntityModel customModel = CommonEntityModel.builder()
            .id(4L)
            .name("custom-entity")
            .displayName("Custom entity")
            .build();

        CommonEntityFetcher baseFetcher = new TestCommonEntityFetcher(
            "user",
            5,
            Collections.singletonMap(baseModel.getId(), baseModel),
            "base-user-model"
        );
        CommonEntityFetcher prioritizedFetcher = new TestCommonEntityFetcher(
            "user",
            0,
            Collections.singletonMap(prioritizedModel.getId(), prioritizedModel),
            "prioritized-model"
        );
        CommonEntityFetcher customFetcher = new TestCommonEntityFetcher(
            "custom",
            1,
            Collections.singletonMap(customModel.getId(), customModel),
            "custom-model"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(Collections.singletonList(baseFetcher));

        CommonEntityFetcherManager manager =
            new CommonEntityFetcherManager(singletonFactory) {
                @Override
                protected List<CommonEntityFetcher> getPrioritizedEntityFetchers(
                    EzySingletonFactory singletonFactory
                ) {
                    return Collections.singletonList(prioritizedFetcher);
                }
            };

        manager.addEntityFetcher(customFetcher);

        Map<String, Set<Long>> idsByType = new HashMap<>();
        idsByType.put("user", Collections.singleton(prioritizedModel.getId()));
        idsByType.put("custom", Collections.singleton(customModel.getId()));

        // when
        CommonEntityModel actualPrioritizedModel =
            manager.getEntityByEntityTypeAndId("user", prioritizedModel.getId());
        Map<String, Map<Long, CommonEntityModel>> mapsByType =
            manager.getEntityMapByEntityIdsByType(idsByType);
        CommonEntityModel fallbackModel =
            manager.getEntityByEntityTypeAndId("missing", 9L);

        // then
        Asserts.assertEquals(
            actualPrioritizedModel.getDisplayName(),
            prioritizedModel.getDisplayName()
        );
        Asserts.assertEquals(
            mapsByType.get("custom").get(customModel.getId()).getName(),
            customModel.getName()
        );
        Asserts.assertEquals(fallbackModel.getId(), 9L);
        Asserts.assertEquals(fallbackModel.getName(), "missing");

        Asserts.assertEquals(
            manager.getAllEntityTypes(),
            Sets.newHashSet("user", "custom")
        );
        Asserts.assertEquals(
            manager.getAllModelNames(),
            Sets.newHashSet("prioritized-model", "custom-model")
        );
    }

    @Test
    public void shouldHandleModuleSpecificFetchersAndOverrides() {
        CommonEntityModel generalModel = CommonEntityModel.builder()
            .id(10L)
            .name("general-user")
            .displayName("General user")
            .build();
        CommonEntityModel moduleModel = CommonEntityModel.builder()
            .id(20L)
            .name("module-user")
            .displayName("Module user")
            .build();
        CommonEntityModel additionalModel = CommonEntityModel.builder()
            .id(30L)
            .name("additional-user")
            .displayName("Additional user")
            .build();

        CommonEntityFetcher generalFetcher = new TestCommonEntityFetcher(
            "user",
            1,
            Collections.singletonMap(generalModel.getId(), generalModel),
            "general-model"
        );
        CommonEntityFetcher moduleFetcher = new TestCommonEntityFetcher(
            "user",
            2,
            Collections.singletonMap(moduleModel.getId(), moduleModel),
            "module-model",
            "module-1"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(Arrays.asList(generalFetcher, moduleFetcher));

        CommonEntityFetcherManager manager = new CommonEntityFetcherManager(
            singletonFactory
        );

        CommonEntityModel moduleEntity = manager.getEntityByModuleNameAndEntityTypeAndId(
            "module-1",
            "user",
            moduleModel.getId()
        );
        CommonEntityModel generalEntity = manager.getEntityByModuleNameAndEntityTypeAndId(
            "missing-module",
            "user",
            generalModel.getId()
        );
        Map<Long, CommonEntityModel> moduleMapBefore =
            manager.getEntityMapByModuleNameAndEntityTypeAndIds(
                "module-1",
                "user",
                Collections.singleton(moduleModel.getId())
            );

        Asserts.assertEquals(moduleEntity.getName(), moduleModel.getName());
        Asserts.assertEquals(generalEntity.getName(), generalModel.getName());
        Asserts.assertEquals(moduleMapBefore.get(moduleModel.getId()).getDisplayName(), moduleModel.getDisplayName());
        Asserts.assertEquals(
            manager.getEntityByModuleNameAndEntityTypeAndId(
                "test",
                generalFetcher.getEntityType(),
                generalModel.getId()
            ),
            generalModel
        );
        Asserts.assertEquals(
            manager.getEntityByModuleNameAndEntityTypeAndId(
                "test",
                "test",
                0
            ),
            CommonEntityModel.defaultEntity(
                0,
                "test"
            )
        );
        Asserts.assertEquals(
            manager.getEntityMapByModuleNameAndEntityTypeAndIds(
                "test",
                "test",
                Collections.emptyList()
            ),
            Collections.emptyMap()
        );
        Asserts.assertEquals(
            manager.getEntityMapByEntityTypeAndIds(
                "test",
                Collections.emptyList()
            ),
            Collections.emptyMap()
        );
        Asserts.assertEquals(
            manager.getEntityMapByModuleNameAndEntityIdsByType(
                "module-1",
                Collections.singletonMap(
                    "user",
                    Collections.singleton(moduleModel.getId())
                )
            ),
            Collections.singletonMap(
                "user",
                Collections.singletonMap(
                    moduleModel.getId(),
                    moduleModel
                )
            ),
            false
        );

        CommonEntityFetcher additionalModuleFetcher = new TestCommonEntityFetcher(
            "user",
            3,
            Collections.singletonMap(additionalModel.getId(), additionalModel),
            "additional-model",
            "module-1"
        );
        manager.addEntityFetcher(additionalModuleFetcher);

        CommonEntityModel additionalEntity = manager.getEntityByModuleNameAndEntityTypeAndId(
            "module-1",
            "user",
            additionalModel.getId()
        );
        CommonEntityFetcher fetcher = manager.getEntityFetcherByModuleNameAndEntityType(
            "module-1",
            "user"
        );
        Map<Long, CommonEntityModel> moduleMapAfter =
            manager.getEntityMapByModuleNameAndEntityTypeAndIds(
                "module-1",
                "user",
                Collections.singleton(additionalModel.getId())
            );

        Asserts.assertEquals(additionalEntity.getName(), additionalModel.getName());
        Asserts.assertEquals(fetcher, additionalModuleFetcher);
        Asserts.assertEquals(moduleMapAfter.get(additionalModel.getId()).getDisplayName(), additionalModel.getDisplayName());
        Asserts.assertEquals(
            manager.getEntityFetcherByModuleNameAndEntityType("another", "user"),
            generalFetcher
        );
        Asserts.assertEquals(
            manager.getAllEntityTypes(),
            Sets.newHashSet("user"),
            false
        );
        Asserts.assertEquals(
            manager.getAllModelNames(),
            Sets.newHashSet("general-model", "additional-model", "module-model"),
            false
        );
    }

    private static final class TestCommonEntityFetcher
        implements CommonEntityFetcher {

        private final String entityType;
        private final String moduleName;
        private final int priority;
        private final Map<Long, CommonEntityModel> entities;
        private final String modelName;

        private TestCommonEntityFetcher(
            String entityType,
            int priority,
            Map<Long, CommonEntityModel> entities,
            String modelName
        ) {
            this(entityType, priority, entities, modelName, "");
        }

        private TestCommonEntityFetcher(
            String entityType,
            int priority,
            Map<Long, CommonEntityModel> entities,
            String modelName,
            String moduleName
        ) {
            this.entityType = entityType;
            this.priority = priority;
            this.entities = new HashMap<>(entities);
            this.modelName = modelName;
            this.moduleName = moduleName != null ? moduleName : "";
        }

        @Override
        public CommonEntityModel getEntityById(long entityId) {
            return entities.get(entityId);
        }

        @Override
        public Map<Long, CommonEntityModel> getEntityMapByIds(
            Collection<Long> entityIds
        ) {
            if (entityIds == null || entityIds.isEmpty()) {
                return Collections.emptyMap();
            }
            return entityIds.stream()
                .filter(entities::containsKey)
                .collect(Collectors.toMap(Function.identity(), entities::get));
        }

        @Override
        public String getEntityType() {
            return entityType;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public String getModelName() {
            return modelName != null ? modelName : entityType;
        }

        @Override
        public String getModuleName() {
            return moduleName;
        }
    }
}
