/*
 * Copyright 2024 youngmonkeys.org
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
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.LastUpdatedAtPageToken;

import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MIN_SQL_DATETIME;

public class LastUpdatedAtPageTokenTest {

    @Test
    public void newLastPageTokenNormalCase() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                1,
                () -> now.plusDays(1L)
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now.plusDays(1L),
                0,
                100,
                false
            )
        );
    }

    @Test
    public void newLastPageTokenEqualsCase() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                100,
                () -> now
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                100,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenNoItemCase() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                0,
                () -> now
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                0,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenIdFetcherNullCase() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                1,
                () -> now.plusDays(1L),
                () -> EzyStrings.NULL
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now.plusDays(1L),
                0,
                100,
                false
            )
        );
    }

    @Test
    public void newLastPageTokenIdFetcherNull2Case() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                1,
                () -> now.plusDays(1L),
                () -> null
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now.plusDays(1L),
                0,
                100,
                false
            )
        );
    }

    @Test
    public void newLastPageTokenNormalCaseIdNumber() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            1L,
            null,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                1,
                () -> now.plusDays(1L),
                () -> 1L
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now.plusDays(1L),
                0L,
                null,
                0,
                100,
                false
            )
        );
    }

    @Test
    public void newLastPageTokenEqualsCaseIdNumber() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0L,
            null,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                100,
                () -> now,
                () -> 1L
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                1L,
                null,
                0,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenNoItemCaseWithIdNumber() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            1L,
            null,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                0,
                () -> now,
                () -> 2L
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                1L,
                null,
                0,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenNullCaseIdNumber() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            1L,
            null,
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                2,
                () -> now,
                () -> null
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                1L,
                null,
                2,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenNormalCaseIdText() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0L,
            "1",
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                1,
                () -> now.plusDays(1L),
                () -> "0"
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now.plusDays(1L),
                0L,
                null,
                0,
                100,
                false
            )
        );
    }

    @Test
    public void newLastPageTokenEqualsCaseIdText() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0L,
            "0",
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                100,
                () -> now,
                () -> "1"
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                0L,
                "1",
                0,
                100,
                true
            )
        );
    }

    @Test
    public void newLastPageTokenNoItemCaseWithIdText() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken instance = new LastUpdatedAtPageToken(
            now,
            0L,
            "1",
            0,
            100,
            true
        );

        // when
        LastUpdatedAtPageToken actual = instance
            .newLastPageToken(
                0,
                () -> now,
                () -> "2"
            );

        // then
        Asserts.assertEquals(
            actual,
            new LastUpdatedAtPageToken(
                now,
                0L,
                "1",
                0,
                100,
                true
            )
        );
    }

    @Test
    public void allArgsConstructorTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LastUpdatedAtPageToken pageToken = new LastUpdatedAtPageToken(
            now,
            0L,
            "0",
            0,
            100,
            false
        );

        // when
        // then
        Asserts.assertEquals(
            pageToken.getUpdatedAt(),
            now
        );
        Asserts.assertEquals(
            pageToken.getIdNumber(),
            0L
        );
        Asserts.assertEquals(
            pageToken.getIdText(),
            "0"
        );
        Asserts.assertEquals(
            pageToken.getOffset(),
            0
        );
        Asserts.assertEquals(
            pageToken.getLimit(),
            100
        );
        Asserts.assertFalse(pageToken.isFetchGreaterThanOrEquals());
    }

    @Test
    public void noArgsConstructorTest() {
        // given
        LastUpdatedAtPageToken pageToken = new LastUpdatedAtPageToken();

        // when
        // then
        Asserts.assertNull(pageToken.getUpdatedAt());
        Asserts.assertEquals(
            pageToken.getIdNumber(),
            0L
        );
        Asserts.assertNull(pageToken.getIdText());
        Asserts.assertEquals(
            pageToken.getOffset(),
            0
        );
        Asserts.assertEquals(
            pageToken.getLimit(),
            0
        );
        Asserts.assertFalse(pageToken.isFetchGreaterThanOrEquals());
    }

    @Test
    public void defaultPageTokenTest() {
        // given
        LastUpdatedAtPageToken pageToken = LastUpdatedAtPageToken
            .defaultPageToken();

        // when
        // then
        Asserts.assertEquals(
            pageToken.getUpdatedAt(),
            MIN_SQL_DATETIME
        );
        Asserts.assertEquals(
            pageToken.getIdNumber(),
            0L
        );
        Asserts.assertNull(pageToken.getIdText());
        Asserts.assertEquals(
            pageToken.getOffset(),
            0
        );
        Asserts.assertEquals(
            pageToken.getLimit(),
            100
        );
        Asserts.assertTrue(pageToken.isFetchGreaterThanOrEquals());
    }

    @Test
    public void jsonTest() throws Exception {
        // given
        LastUpdatedAtPageToken pageToken = LastUpdatedAtPageToken
            .defaultPageToken();
        ObjectMapper objectMapper = new ObjectMapperBuilder()
            .build();

        // when
        String json = objectMapper.writeValueAsString(pageToken);

        // then
        LastUpdatedAtPageToken value = objectMapper
            .readValue(json, LastUpdatedAtPageToken.class);
        Asserts.assertEquals(value, pageToken);
    }
}
