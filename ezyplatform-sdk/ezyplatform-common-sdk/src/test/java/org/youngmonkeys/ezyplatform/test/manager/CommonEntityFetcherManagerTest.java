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
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.fetcher.CommonEntityFetcher;
import org.youngmonkeys.ezyplatform.manager.CommonEntityFetcherManager;
import org.youngmonkeys.ezyplatform.model.CommonEntityModel;

import java.lang.reflect.Field;
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

    @Test
    public void shouldPreferHigherPriorityFetchersPerModuleFromFactory() {
        CommonEntityModel lowPriorityModel = CommonEntityModel.builder()
            .id(41L)
            .name("module-low")
            .displayName("Module low priority")
            .build();
        CommonEntityModel highPriorityModel = CommonEntityModel.builder()
            .id(42L)
            .name("module-high")
            .displayName("Module high priority")
            .build();

        CommonEntityFetcher lowPriorityModuleFetcher = new TestCommonEntityFetcher(
            "module-user",
            1,
            Collections.singletonMap(lowPriorityModel.getId(), lowPriorityModel),
            "module-low-model",
            "module-A"
        );
        CommonEntityFetcher highPriorityModuleFetcher = new TestCommonEntityFetcher(
            "module-user",
            5,
            Collections.singletonMap(highPriorityModel.getId(), highPriorityModel),
            "module-high-model",
            "module-A"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(Arrays.asList(lowPriorityModuleFetcher, highPriorityModuleFetcher));

        CommonEntityFetcherManager manager = new CommonEntityFetcherManager(singletonFactory);

        CommonEntityFetcher moduleFetcher = manager.getEntityFetcherByModuleNameAndEntityType(
            "module-A",
            "module-user"
        );
        CommonEntityModel fetchedModel = manager.getEntityByModuleNameAndEntityTypeAndId(
            "module-A",
            "module-user",
            highPriorityModel.getId()
        );

        Asserts.assertEquals(moduleFetcher, highPriorityModuleFetcher);
        Asserts.assertEquals(fetchedModel.getName(), highPriorityModel.getName());
    }

    @Test
    public void shouldSkipBlankModuleNamesWhenBuildingModuleMap() throws Exception {
        CommonEntityModel generalModel = CommonEntityModel.builder()
            .id(50L)
            .name("global-user")
            .displayName("Global user")
            .build();

        CommonEntityFetcher generalFetcher = new TestCommonEntityFetcher(
            "global",
            2,
            Collections.singletonMap(generalModel.getId(), generalModel),
            "global-model",
            ""
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(Collections.singletonList(generalFetcher));

        CommonEntityFetcherManager manager = new CommonEntityFetcherManager(singletonFactory);
        manager.getEntityFetcherByModuleNameAndEntityType("", "global");

        Field field = CommonEntityFetcherManager.class
            .getDeclaredField("accountingEntityFetcherByEntityTypeByModuleName");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        EzyLazyInitializer<Map<String, Map<String, CommonEntityFetcher>>> initializer =
            (EzyLazyInitializer<Map<String, Map<String, CommonEntityFetcher>>>) field.get(manager);
        Map<String, Map<String, CommonEntityFetcher>> mapByModuleName = initializer.get();

        Asserts.assertFalse(mapByModuleName.containsKey(""));
        Asserts.assertEquals(
            manager.getEntityByEntityTypeAndId("global", generalModel.getId()).getName(),
            generalModel.getName()
        );
    }

    @Test
    public void priorityFetchersTest() {
        // given
        List<CommonEntityFetcher> prioritizedEntityFetcher = new ArrayList<>();
        CommonEntityFetcher fetcher1 = mock(CommonEntityFetcher.class);
        when(fetcher1.getEntityTypes()).thenReturn(new String[] {"entity-type1"});
        prioritizedEntityFetcher.add(fetcher1);

        CommonEntityFetcher fetcher2 = mock(CommonEntityFetcher.class);
        when(fetcher2.getModuleName()).thenReturn("module-1");
        when(fetcher2.getEntityTypes()).thenReturn(new String[] {"entity-type2"});
        prioritizedEntityFetcher.add(fetcher2);

        List<CommonEntityFetcher> fetchers = new ArrayList<>();
        CommonEntityFetcher fetcher3 = mock(CommonEntityFetcher.class);
        when(fetcher3.getEntityTypes()).thenReturn(new String[] {"entity-type3"});
        fetchers.add(fetcher3);

        CommonEntityFetcher fetcher4 = mock(CommonEntityFetcher.class);
        when(fetcher4.getEntityTypes()).thenReturn(new String[] {"entity-type3"});
        when(fetcher4.getPriority()).thenReturn(2);
        fetchers.add(fetcher4);

        CommonEntityFetcher fetcher5 = mock(CommonEntityFetcher.class);
        when(fetcher5.getEntityTypes()).thenReturn(new String[] {"entity-type3"});
        when(fetcher5.getPriority()).thenReturn(1);
        fetchers.add(fetcher5);

        CommonEntityFetcher fetcher6 = mock(CommonEntityFetcher.class);
        when(fetcher6.getModuleName()).thenReturn("module-2");
        when(fetcher6.getEntityTypes()).thenReturn(new String[] {"entity-type4"});
        fetchers.add(fetcher6);

        CommonEntityFetcher fetcher7 = mock(CommonEntityFetcher.class);
        when(fetcher7.getModuleName()).thenReturn("module-2");
        when(fetcher7.getEntityTypes()).thenReturn(new String[] {"entity-type4"});
        when(fetcher7.getPriority()).thenReturn(2);
        fetchers.add(fetcher7);

        CommonEntityFetcher fetcher8 = mock(CommonEntityFetcher.class);
        when(fetcher8.getModuleName()).thenReturn("module-2");
        when(fetcher8.getEntityTypes()).thenReturn(new String[] {"entity-type4"});
        when(fetcher8.getPriority()).thenReturn(1);
        fetchers.add(fetcher8);

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(CommonEntityFetcher.class))
            .thenReturn(fetchers);

        // when
        TestCommonEntityFetcherManager instance = new TestCommonEntityFetcherManager(
            singletonFactory,
            prioritizedEntityFetcher
        );

        // then
        Asserts.assertEquals(
            instance.getEntityFetcherByModuleNameAndEntityType(
                "module-1",
                "entity-type2"
            ),
            fetcher2
        );
        Asserts.assertEquals(
            instance.getEntityFetcherByModuleNameAndEntityType(
                "module-2",
                "entity-type4"
            ),
            fetcher7
        );
        Asserts.assertEquals(
            instance.getEntityFetcherByEntityType(
                "entity-type3"
            ),
            fetcher4
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

    public static class TestCommonEntityFetcherManager
        extends CommonEntityFetcherManager {

        private final List<CommonEntityFetcher> prioritizedEntityFetcher;

        public TestCommonEntityFetcherManager(
            EzySingletonFactory singletonFactory,
            List<CommonEntityFetcher> prioritizedEntityFetcher
        ) {
            super(singletonFactory);
            this.prioritizedEntityFetcher = prioritizedEntityFetcher;
        }

        @Override
        protected List<CommonEntityFetcher> getPrioritizedEntityFetchers(
            EzySingletonFactory singletonFactory
        ) {
            return prioritizedEntityFetcher;
        }
    }
}
