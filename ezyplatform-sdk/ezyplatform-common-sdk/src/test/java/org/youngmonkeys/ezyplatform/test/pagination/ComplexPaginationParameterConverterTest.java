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

package org.youngmonkeys.ezyplatform.test.pagination;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.ComplexPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.DefaultPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.pagination.PaginationParameterConverter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class ComplexPaginationParameterConverterTest {

    @Test
    public void derSerTest() {
        // given
        TestComplexPaginationParameterConverter instance =
            new TestComplexPaginationParameterConverter(
                new DefaultPaginationParameterConverter(
                    new ObjectMapper()
                )
            );

        // when
        Long id = RandomUtil.randomLong();
        String pageToken = instance.serialize(
            SortOrder.ID_DESC,
            new Model(id)
        );

        // then
        TestPaginationParameter value = instance.deserialize(pageToken);
        Asserts.assertEquals(
            value,
            new TestPaginationParameter(id)
        );
    }

    @Test
    public void registerPaginationParameterTest() throws Exception {
        // given
        TestEmptyComplexPaginationParameterConverter instance =
            new TestEmptyComplexPaginationParameterConverter(
                new DefaultPaginationParameterConverter(
                    new ObjectMapper()
                )
            );
        instance.registerPaginationParameter(
            SortOrder.ID_DESC,
            TestPaginationParameter.class,
            it -> new TestPaginationParameter(it.getId())
        );

        // when
        Long id = RandomUtil.randomLong();
        String pageToken = instance.serialize(
            SortOrder.ID_DESC,
            new Model(id)
        );

        // then
        TestPaginationParameter value = instance.deserialize(pageToken);
        Asserts.assertEquals(
            value,
            new TestPaginationParameter(id)
        );
        Asserts.assertEquals(
            instance.getDefaultPageToken(SortOrder.ID_DESC),
            EzyBase64.encodeUtf(
                new ObjectMapper().writeValueAsString(
                    new ComplexPaginationParameterConverter
                        .PaginationParameterWrapper<>(
                        SortOrder.ID_DESC,
                        Collections.singletonMap(
                            "id",
                            null
                        )
                    )
                )
            )
        );
    }

    public static class TestComplexPaginationParameterConverter
        extends ComplexPaginationParameterConverter<SortOrder, Model> {

        public TestComplexPaginationParameterConverter(
            PaginationParameterConverter converter
        ) {
            super(converter);
        }

        @Override
        protected void mapPaginationParametersToTypes(
            Map<SortOrder, Class<?>> map
        ) {
            map.put(SortOrder.ID_DESC, TestPaginationParameter.class);
        }

        @Override
        protected void addPaginationParameterExtractors(
            Map<SortOrder, Function<Model, Object>> map
        ) {
            map.put(
                SortOrder.ID_DESC,
                it -> new TestPaginationParameter(it.getId())
            );
        }
    }

    public static class TestEmptyComplexPaginationParameterConverter
        extends ComplexPaginationParameterConverter<SortOrder, Model> {

        public TestEmptyComplexPaginationParameterConverter(
            PaginationParameterConverter converter
        ) {
            super(converter);
        }

        @Override
        protected void mapPaginationParametersToTypes(
            Map<SortOrder, Class<?>> map
        ) {}

        @Override
        protected void addPaginationParameterExtractors(
            Map<SortOrder, Function<Model, Object>> map
        ) {}
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Model {
        private Long id;
    }

    public enum SortOrder {
        ID_DESC
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestPaginationParameter {

        private Long id;

        @SuppressWarnings("unused")
        public SortOrder sortOrder() {
            return SortOrder.ID_DESC;
        }
    }
}
