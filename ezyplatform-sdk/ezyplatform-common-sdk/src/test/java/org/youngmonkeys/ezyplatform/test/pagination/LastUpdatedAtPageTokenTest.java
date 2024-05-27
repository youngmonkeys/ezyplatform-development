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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.LastUpdatedAtPageToken;

import java.time.LocalDateTime;

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
                false
            )
        );
    }
}
