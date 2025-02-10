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

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MIN_SQL_DATETIME;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LastUpdatedAtPageToken {
    private LocalDateTime updatedAt;
    private long idNumber;
    private String idText;
    private int offset;
    private int limit;
    private boolean fetchGreaterThanOrEquals;

    public static final int DEFAULT_LIMIT = 100;

    public LastUpdatedAtPageToken(
        LocalDateTime updatedAt,
        int offset,
        int limit,
        boolean fetchGreaterThanOrEquals
    ) {
        this.updatedAt = updatedAt;
        this.offset = offset;
        this.limit = limit;
        this.fetchGreaterThanOrEquals = fetchGreaterThanOrEquals;
    }

    public LastUpdatedAtPageToken newLastPageToken(
        int itemCount,
        Supplier<LocalDateTime> lastUpdatedAtSupplier,
        Supplier<Object> lastIdSupplier
    ) {
        long newLastIdNumber = idNumber;
        String newLastIdText = idText;
        LocalDateTime newLastUpdatedAt = updatedAt;
        if (itemCount > 0)  {
            newLastUpdatedAt = lastUpdatedAtSupplier.get();
            Object newLastId = lastIdSupplier.get();
            if (newLastId != null) {
                if (newLastId instanceof Number) {
                    newLastIdNumber = ((Number) newLastId)
                        .longValue();
                } else {
                    newLastIdText = String.valueOf(newLastId);
                }
            }
        }
        return new LastUpdatedAtPageToken(
            newLastUpdatedAt,
            newLastIdNumber,
            newLastIdText,
            offset,
            limit,
            Boolean.TRUE
        );
    }

    public static LastUpdatedAtPageToken defaultPageToken() {
        return defaultPageToken(MIN_SQL_DATETIME);
    }

    public static LastUpdatedAtPageToken defaultPageToken(
        LocalDateTime updatedAt
    ) {
        return new LastUpdatedAtPageToken(
            updatedAt,
            0,
            DEFAULT_LIMIT,
            true
        );
    }
}
