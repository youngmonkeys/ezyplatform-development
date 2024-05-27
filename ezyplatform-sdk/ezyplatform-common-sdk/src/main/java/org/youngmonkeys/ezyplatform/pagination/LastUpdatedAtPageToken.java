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

package org.youngmonkeys.ezyplatform.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.youngmonkeys.ezyplatform.constant.CommonConstants;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LastUpdatedAtPageToken {
    private LocalDateTime updatedAt;
    private int offset;
    private int limit;
    private boolean fetchGreaterThanOrEquals;

    public static final int DEFAULT_LIMIT = 100;

    public LastUpdatedAtPageToken newLastPageToken(
        int itemCount,
        Supplier<LocalDateTime> lastUpdatedAtSupplier
    ) {
        int newOffset = 0;
        LocalDateTime newLastUpdatedAt = updatedAt;
        if (itemCount > 0) {
            newLastUpdatedAt = lastUpdatedAtSupplier.get();
            if (newLastUpdatedAt.equals(updatedAt)) {
                newOffset = offset + itemCount;
            }
        }
        return new LastUpdatedAtPageToken(
            newLastUpdatedAt,
            newOffset,
            limit,
            itemCount >= limit
        );
    }

    public static LastUpdatedAtPageToken defaultPageToken() {
        return new LastUpdatedAtPageToken(
            CommonConstants.MIN_SQL_DATETIME,
            0,
            DEFAULT_LIMIT,
            true
        );
    }
}
