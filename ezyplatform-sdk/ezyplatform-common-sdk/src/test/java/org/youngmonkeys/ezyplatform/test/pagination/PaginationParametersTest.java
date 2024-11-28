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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.SortOrder;

import static org.youngmonkeys.ezyplatform.pagination.PaginationParameters.*;

public class PaginationParametersTest {

    @Test
    public void makeOrderByAsc1FieldNextPageTest() {
        // given
        // when
        String actual = makeOrderByAsc(
            true,
            "hello"
        );

        // then
        Asserts.assertEquals(actual, "e.hello ASC");
    }

    @Test
    public void makeOrderByAsc1FieldPrevPageTest() {
        // given
        // when
        String actual = makeOrderByAsc(
            false,
            "hello"
        );

        // then
        Asserts.assertEquals(actual, "e.hello DESC");
    }

    @Test
    public void makeOrderByAsc2FieldsNextPageTest() {
        // given
        // when
        String actual = makeOrderByAsc(
            true,
            "hello",
            "world"
        );

        // then
        Asserts.assertEquals(actual, "e.hello ASC, e.world ASC");
    }

    @Test
    public void makeOrderByAsc2FieldsPrevPageTest() {
        // given
        // when
        String actual = makeOrderByAsc(
            false,
            "hello",
            "world"
        );

        // then
        Asserts.assertEquals(actual, "e.hello DESC, e.world DESC");
    }

    @Test
    public void makeOrderByDesc2FieldsNextPageTest() {
        // given
        // when
        String actual = makeOrderByDesc(
            true,
            "hello",
            "world"
        );

        // then
        Asserts.assertEquals(actual, "e.hello DESC, e.world DESC");
    }

    @Test
    public void makeOrderByDesc2FieldsPrevPageTest() {
        // given
        // when
        String actual = makeOrderByDesc(
            false,
            "hello",
            "world"
        );

        // then
        Asserts.assertEquals(actual, "e.hello ASC, e.world ASC");
    }

    @Test
    public void makeOrderByTest() {
        // given
        // when
        String actual = makeOrderBy(
            SortOrder.ASC,
            true,
            new String[] { "e", "a" },
            new String[] { "hello", "world" }
        );

        // then
        Asserts.assertEquals(actual, "e.hello ASC, a.world ASC");
    }

    @Test
    public void makePaginationConditionAsc1FieldNextPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            true,
            "hello"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello"
        );
    }

    @Test
    public void makePaginationConditionDesc1FieldNextPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            true,
            "hello"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello"
        );
    }

    @Test
    public void makePaginationConditionAsc2FieldsNextPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            true,
            "hello",
            "young"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello " +
                "OR (e.hello = :hello AND e.young > :young)"
        );
    }

    @Test
    public void makePaginationConditionAsc2FieldsPrevPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            false,
            "hello",
            "young"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello < :hello " +
                "OR (e.hello = :hello AND e.young < :young)"
        );
    }

    @Test
    public void makePaginationConditionAsc3FieldsNextPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            true,
            "hello",
            "young",
            "monkeys"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello " +
                "OR (e.hello = :hello AND e.young > :young " +
                "OR (e.hello = :hello AND e.young = :young AND e.monkeys > :monkeys))"
        );
    }

    @Test
    public void makePaginationConditionAsc3FieldsPrevPageTest() {
        // given
        // when
        String actual = makePaginationConditionAsc(
            false,
            "hello",
            "young",
            "monkeys"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello < :hello " +
                "OR (e.hello = :hello AND e.young < :young " +
                "OR (e.hello = :hello AND e.young = :young AND e.monkeys < :monkeys))"
        );
    }

    @Test
    public void makePaginationConditionDesc3FieldsNextPageTest() {
        // given
        // when
        String actual = makePaginationConditionDesc(
            true,
            "hello",
            "young",
            "monkeys"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello < :hello " +
                "OR (e.hello = :hello AND e.young < :young " +
                "OR (e.hello = :hello AND e.young = :young AND e.monkeys < :monkeys))"
        );
    }

    @Test
    public void makePaginationConditionDesc3FieldsPrevPageTest() {
        // given
        // when
        String actual = makePaginationConditionDesc(
            false,
            "hello",
            "young",
            "monkeys"
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello " +
                "OR (e.hello = :hello AND e.young > :young " +
                "OR (e.hello = :hello AND e.young = :young AND e.monkeys > :monkeys))"
        );
    }

    @Test
    public void makePaginationConditionTest() {
        // given
        // when
        String actual = makePaginationCondition(
            SortOrder.DESC,
            false,
            new String[] { "e", "a", "b" },
            new String[] { "hello", "young", "monkeys" }
        );

        // then
        Asserts.assertEquals(
            actual,
            "e.hello > :hello " +
                "OR (e.hello = :hello AND a.young > :young " +
                "OR (e.hello = :hello AND a.young = :young AND b.monkeys > :monkeys))"
        );
    }
}
