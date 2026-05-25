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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import com.tvd12.ezyfox.util.Next;
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.result.StatusResult;
import org.youngmonkeys.ezyplatform.result.TypeResult;
import org.youngmonkeys.ezyplatform.result.UpdatedAtValueResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.META_KEY_SLUG;
import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_MEDIA;

public interface MediaRepository extends EzyDatabaseRepository<Long, Media> {

    @EzyQuery(
        "SELECT DISTINCT e.type FROM Media e " +
            "ORDER BY e.type ASC"
    )
    List<TypeResult> findAllMediaTypes();

    @EzyQuery(
        "SELECT DISTINCT e.status " +
        "FROM Media e " +
        "ORDER BY e.status ASC"
    )
    List<StatusResult> findAllMediaStatuses();

    @EzyQuery(
        "SELECT e FROM Media e " +
            "WHERE e.name = ?0 OR e.originalName = ?0"
    )
    Media findByNameOrOriginalName(
        String name
    );

    @EzyQuery(
        "SELECT e FROM Media e " +
            "INNER JOIN DataMeta a ON e.id = a.dataId " +
            "WHERE a.dataType = '" + TABLE_NAME_MEDIA + "' " +
            "AND a.metaKey = '" + META_KEY_SLUG + "' " +
            "AND a.metaValue = ?0"
    )
    Media findBySlug(
        String name
    );

    @EzyQuery(
        "SELECT e.ownerAdminId FROM Media e " +
            "WHERE e.id = ?0"
    )
    IdResult findOwnerAdminIdById(
        long mediaId
    );

    @EzyQuery(
        "SELECT e.ownerAdminId FROM Media e " +
            "WHERE e.name = ?0 OR e.originalName = ?0"
    )
    IdResult findOwnerAdminIdByNameOrOriginalName(
        String mediaName
    );

    @EzyQuery(
        "SELECT e.ownerUserId FROM Media e " +
            "WHERE e.id = ?0"
    )
    IdResult findOwnerUserIdById(
        long mediaId
    );

    @EzyQuery(
        "SELECT e.ownerUserId FROM Media e " +
            "WHERE e.name = ?0 OR e.originalName = ?0"
    )
    IdResult findOwnerUserIdByNameOrOriginalName(
        String mediaName
    );

    @EzyQuery(
        "SELECT e.updatedAt FROM Media e " +
            "WHERE e.name = ?0 OR e.originalName = ?0"
    )
    UpdatedAtValueResult findUpdatedAtByNameOrOriginalName(
        String mediaName
    );

    @EzyQuery(
        "SELECT e FROM Media e " +
            "WHERE e.updatedAt > ?0 " +
            "OR (e.updatedAt = ?0 AND e.id > ?1)" +
            "ORDER BY e.updatedAt ASC, e.id ASC"
    )
    List<Media> findMediaByUpdatedAtAndIdPaginationAsc(
        LocalDateTime updatedAtInclusive,
        long idExclusive,
        Next next
    );

    @EzyQuery(
        "SELECT e.updatedAt FROM Media e ORDER BY e.updatedAt ASC"
    )
    UpdatedAtValueResult findFirstUpdatedAt();
}
