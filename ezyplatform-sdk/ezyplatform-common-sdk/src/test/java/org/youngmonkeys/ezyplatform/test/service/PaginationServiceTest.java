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

package org.youngmonkeys.ezyplatform.test.service;

import com.tvd12.test.assertion.Asserts;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperation;
import org.youngmonkeys.ezyplatform.service.PaginationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaginationServiceTest {

    @Test
    public void getFirstPageTest() {
        // given
        InternalPagination sut = new InternalPagination();

        // when
        PaginationModel<Model> actual = sut.getFirstPage(2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Arrays.asList(
                    new Model(1, "1"),
                    new Model(2, "2")
                )
            )
            .count(2)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next("Mg==")
                    .prev(null)
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(true)
                    .hasPrevious(false)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    @Test
    public void getNextPageHasNextTest() {
        // given
        InternalPagination sut = new InternalPagination();
        String pageToken = "Mg==";

        // when
        PaginationModel<Model> actual = sut.getNextPage(pageToken, 2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Arrays.asList(
                    new Model(3, "3"),
                    new Model(4, "4")
                )
            )
            .count(2)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next("NA==")
                    .prev("Mw==")
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(true)
                    .hasPrevious(true)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    @Test
    public void getNextPageHasNoNextTest() {
        // given
        InternalPagination sut = new InternalPagination();
        String pageToken = "NA==";

        // when
        PaginationModel<Model> actual = sut.getNextPage(pageToken, 2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Collections.singletonList(
                    new Model(5, "5")
                )
            )
            .count(1)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next(null)
                    .prev("NQ==")
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(false)
                    .hasPrevious(true)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    @Test
    public void getLastPageTest() {
        // given
        InternalPagination sut = new InternalPagination();

        // when
        PaginationModel<Model> actual = sut.getLastPage(2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Arrays.asList(
                    new Model(4, "4"),
                    new Model(5, "5")
                )
            )
            .count(2)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next(null)
                    .prev("NA==")
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(false)
                    .hasPrevious(true)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    @Test
    public void getPreviousPageHasPreviousTest() {
        // given
        InternalPagination sut = new InternalPagination();
        String pageToken = "NQ==";

        // when
        PaginationModel<Model> actual = sut.getPreviousPage(pageToken, 2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Arrays.asList(
                    new Model(3, "3"),
                    new Model(4, "4")
                )
            )
            .count(2)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next("NA==")
                    .prev("Mw==")
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(true)
                    .hasPrevious(true)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    @Test
    public void getPreviousPageHasNoPreviousTest() {
        // given
        InternalPagination sut = new InternalPagination();
        String pageToken = "Mg==";

        // when
        PaginationModel<Model> actual = sut.getPreviousPage(pageToken, 2);

        // then
        PaginationModel<Model> expected = PaginationModel.<Model>builder()
            .items(
                Collections.singletonList(
                    new Model(1, "1")
                )
            )
            .count(1)
            .total(5)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next("MQ==")
                    .prev(null)
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(true)
                    .hasPrevious(false)
                    .build()
            )
            .build();
        Asserts.assertEquals(actual, expected, false);
    }

    private static class InternalPagination
        extends PaginationService<Model, Void, Integer> {

        private final List<Model> models = Arrays.asList(
            new Model(1, "1"),
            new Model(2, "2"),
            new Model(3, "3"),
            new Model(4, "4"),
            new Model(5, "5")
        );

        @Override
        protected RxOperation getFirstItems(Void matchingValue, int limit) {
            return Reactive.single(
                models.subList(0, limit)
            );
        }

        @Override
        protected RxOperation getNextItemsExclusive(Void matchingValue, Integer paginationValue, int limit) {
            List<Model> answer = new ArrayList<>();
            for (Model model : models) {
                if (model.index > paginationValue) {
                    answer.add(model);
                }
                if (answer.size() >= limit) {
                    break;
                }
            }
            return Reactive.single(answer);
        }

        @Override
        protected RxOperation getPreviousItemsExclusive(Void matchingValue, Integer paginationValue, int limit) {
            List<Model> answer = new ArrayList<>();
            for (int i = models.size() - 1 ; i >= 0 ; --i) {
                if (models.get(i).index < paginationValue) {
                    answer.add(models.get(i));
                }
                if (answer.size() >= limit) {
                    break;
                }
            }
            return Reactive.single(answer);
        }

        @Override
        protected RxOperation getLastItems(Void matchingValue, int limit) {
            List<Model> answer = models.subList(models.size() - limit, models.size());
            Collections.reverse(answer);
            return Reactive.single(answer);
        }

        @Override
        protected long getTotalItems(Void matchingValue) {
            return models.size();
        }

        @Override
        protected String serializeToPageToken(Model value) {
            return value.index.toString();
        }

        @Override
        protected Integer deserializePageToken(String pageToken) {
            return Integer.valueOf(pageToken);
        }

        @Override
        protected long getTimestamp() {
            return 0;
        }
    }

    @ToString
    @AllArgsConstructor
    private static class Model {
        private Integer index;
        private String name;
    }
}
