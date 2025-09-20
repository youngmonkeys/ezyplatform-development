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

package org.youngmonkeys.ezyplatform.test.service;

import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.test.assertion.Asserts;
import lombok.*;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.pagination.CommonPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationRepository;
import org.youngmonkeys.ezyplatform.service.CommonPaginationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_PAGINATION_COUNT_LIMIT;

public class CommonPaginationServiceTest {

    @Test
    public void getFirstPageTest() {
        // given
        InternalPaginationRepository paginationRepository = mock(
            InternalPaginationRepository.class
        );
        List<Entity> entities = Arrays.asList(
            new Entity(1),
            new Entity(2),
            new Entity(3)
        );
        when(
            paginationRepository.findNextElements(
                any(Filter.class),
                any(InternalPaginationParameter.class),
                any(int.class)
            )
        ).thenReturn(entities.subList(0, 2));
        when(
            paginationRepository.countElements(null)
        ).thenReturn((long) entities.size());

        PaginationParameterConverter converter = mock(
            PaginationParameterConverter.class
        );
        when(
            converter.serialize(any())
        ).thenReturn("1");
        InternalPaginationParameterConverter paginationParameterConverter =
            new InternalPaginationParameterConverter(
                converter
            );

        InternalPaginationService instance = new InternalPaginationService(
            paginationRepository,
            paginationParameterConverter
        );

        // when
        PaginationModel<Model> pagination1 = instance.getFirstPage(1);

        // then
        Asserts.assertEquals(
            pagination1,
            PaginationModel.builder()
                .items(
                    Collections.singletonList(new Model(1))
                )
                .count(1)
                .total(3)
                .timestamp(pagination1.getTimestamp())
                .pageToken(
                    PaginationModel.PageToken.builder()
                        .next(EzyBase64.encodeUtf("1"))
                        .prev(null)
                        .build()
                )
                .continuation(
                    PaginationModel.Continuation.builder()
                        .hasNext(true)
                        .hasPrevious(false)
                        .build()
                ),
            false
        );

        verify(paginationRepository, times(1))
            .findNextElements(
                any(Filter.class),
                any(InternalPaginationParameter.class),
                any(int.class)
            );
        verify(paginationRepository, times(1))
            .findFirstElements(
                any(Filter.class),
                any(int.class),
                any(int.class)
            );
        verify(paginationRepository, times(1))
            .countElements(any(Filter.class));
        verify(paginationRepository, times(1))
            .findSettingValue(SETTING_NAME_PAGINATION_COUNT_LIMIT);
        verifyNoMoreInteractions(paginationRepository);

        verify(converter, times(2))
            .serialize(any());
        verify(converter, times(2))
            .serializeToMap(any());
        verifyNoMoreInteractions(converter);
    }

    @Test
    public void getLastPageTest() {
        // given
        InternalPaginationRepository paginationRepository = mock(
            InternalPaginationRepository.class
        );
        List<Entity> entities = Arrays.asList(
            new Entity(3),
            new Entity(2),
            new Entity(1)
        );
        when(
            paginationRepository.findPreviousElements(
                any(Filter.class),
                any(InternalPaginationParameter.class),
                any(int.class)
            )
        ).thenReturn(entities);
        when(
            paginationRepository.findSettingValue(
                SETTING_NAME_PAGINATION_COUNT_LIMIT
            )
        ).thenReturn("1000");
        when(
            paginationRepository.countElements(null)
        ).thenReturn((long) entities.size());

        PaginationParameterConverter converter = mock(
            PaginationParameterConverter.class
        );
        when(
            converter.serialize(any())
        ).thenReturn("3");
        InternalPaginationParameterConverter paginationParameterConverter =
            new InternalPaginationParameterConverter(
                converter
            );

        InternalPaginationService instance = new InternalPaginationService(
            paginationRepository,
            paginationParameterConverter
        );

        // when
        PaginationModel<Model> pagination1 = instance.getLastPage(2);

        // then
        Asserts.assertEquals(
            pagination1,
            PaginationModel.builder()
                .items(
                    Arrays.asList(
                        new Model(2),
                        new Model(3)
                    )
                )
                .count(2)
                .total(3)
                .timestamp(pagination1.getTimestamp())
                .pageToken(
                    PaginationModel.PageToken.builder()
                        .next(null)
                        .prev(EzyBase64.encodeUtf("3"))
                        .build()
                )
                .continuation(
                    PaginationModel.Continuation.builder()
                        .hasNext(false)
                        .hasPrevious(true)
                        .build()
                ),
            false
        );

        verify(paginationRepository, times(1))
            .findPreviousElements(
                any(Filter.class),
                any(InternalPaginationParameter.class),
                any(int.class)
            );
        verify(paginationRepository, times(1))
            .findFirstElements(
                any(Filter.class),
                any(int.class),
                any(int.class)
            );
        verify(paginationRepository, times(1))
            .countElements(any(Filter.class));
        verify(paginationRepository, times(1))
            .findSettingValue(SETTING_NAME_PAGINATION_COUNT_LIMIT);
        verifyNoMoreInteractions(paginationRepository);

        verify(converter, times(2))
            .serialize(any());
        verify(converter, times(2))
            .serializeToMap(any());
        verifyNoMoreInteractions(converter);
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Entity {
        private long id;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Model {
        private long id;
    }

    private static class Filter implements CommonStorageFilter {}

    @AllArgsConstructor
    @NoArgsConstructor
    public static class InternalPaginationParameter
        implements CommonPaginationParameter {

        public Long idGt;

        @Override
        public boolean isEmpty() {
            return idGt == null;
        }

        @Override
        public String paginationCondition(boolean nextPage) {
            return nextPage ? "e.id > 0" : "e.id < 0";
        }

        @Override
        public String orderBy(boolean nextPage) {
            return nextPage ? "e.id ASC" : "e.id DESC";
        }

        @Override
        public String sortOrder() {
            return "ID_ASC";
        }
    }

    private static class InternalPaginationRepository
        extends PaginationRepository<Filter, InternalPaginationParameter, Long, Entity> {}

    private static class InternalPaginationParameterConverter
        extends ComplexPaginationParameterConverter<String, Model> {

        public InternalPaginationParameterConverter(
            PaginationParameterConverter converter
        ) {
            super(converter);
        }

        @Override
        protected void mapPaginationParametersToTypes(
            Map<String, Class<?>> map
        ) {
            map.put("ID_ASC", InternalPaginationParameter.class);
        }

        @Override
        protected void addPaginationParameterExtractors(
            Map<String, Function<Model, Object>> map
        ) {
            map.put(
                "ID_ASC",
                model -> new InternalPaginationParameter(model.getId())
            );
        }
    }

    private static class InternalPaginationService
        extends CommonPaginationService<
            Model,
            Filter,
            InternalPaginationParameter,
            Long,
            Entity
        > {

        public InternalPaginationService(
            InternalPaginationRepository repository,
            InternalPaginationParameterConverter paginationParameterConverter
        ) {
            super(repository, paginationParameterConverter);
        }

        @Override
        protected Model convertEntity(Entity entity) {
            return new Model(entity.getId());
        }

        @Override
        protected InternalPaginationParameter defaultPaginationParameter() {
            return new InternalPaginationParameter();
        }
    }
}
