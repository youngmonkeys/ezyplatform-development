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

package org.youngmonkeys.ezyplatform.test.model;

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.model.PaginationModel;

import java.util.Arrays;

public class PaginationModelTest {

    @Test
    public void test() {
        // given
        int count = RandomUtil.randomSmallInt();
        int total = RandomUtil.randomSmallInt();
        String next = RandomUtil.randomShortAlphabetString();
        String prev = RandomUtil.randomShortAlphabetString();
        boolean hasNext = RandomUtil.randomBoolean();
        boolean hasPrev = RandomUtil.randomBoolean();
        PaginationModel<Integer> pagination = PaginationModel
            .<Integer>builder()
            .items(Arrays.asList(1, 2, 3))
            .count(count)
            .total(total)
            .pageToken(
                PaginationModel.PageToken.builder()
                    .next(next)
                    .prev(prev)
                    .build()
            )
            .continuation(
                PaginationModel.Continuation.builder()
                    .hasNext(hasNext)
                    .hasPrevious(hasPrev)
                    .build()
            )
            .build();

        // when
        // then
        Asserts.assertEquals(
            pagination.map(String::valueOf),
            PaginationModel
                .<String>builder()
                .items(Arrays.asList("1", "2", "3"))
                .count(count)
                .total(total)
                .pageToken(
                    PaginationModel.PageToken.builder()
                        .next(next)
                        .prev(prev)
                        .build()
                )
                .continuation(
                    PaginationModel.Continuation.builder()
                        .hasNext(hasNext)
                        .hasPrevious(hasPrev)
                        .build()
                )
                .build(),
            false
        );

        Asserts.assertEquals(
            pagination.mapIndex((value, index) -> value + "-" + index),
            PaginationModel
                .<String>builder()
                .items(Arrays.asList("1-0", "2-1", "3-2"))
                .count(count)
                .total(total)
                .pageToken(
                    PaginationModel.PageToken.builder()
                        .next(next)
                        .prev(prev)
                        .build()
                )
                .continuation(
                    PaginationModel.Continuation.builder()
                        .hasNext(hasNext)
                        .hasPrevious(hasPrev)
                        .build()
                )
                .build(),
            false
        );
    }
}
