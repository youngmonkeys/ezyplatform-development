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
import org.youngmonkeys.ezyplatform.entity.Media;
import org.youngmonkeys.ezyplatform.result.IdResult;
import org.youngmonkeys.ezyplatform.result.StatusResult;
import org.youngmonkeys.ezyplatform.result.TypeResult;

import java.util.List;

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
}
