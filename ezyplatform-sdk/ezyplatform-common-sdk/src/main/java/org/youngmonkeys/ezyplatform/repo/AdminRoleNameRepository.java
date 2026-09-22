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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import com.tvd12.ezyfox.util.Next;
import org.youngmonkeys.ezyplatform.entity.AdminRoleName;
import org.youngmonkeys.ezyplatform.result.UpdatedAtValueResult;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AdminRoleNameRepository
    extends EzyDatabaseRepository<Long, AdminRoleName> {

    @EzyQuery(
        "SELECT e FROM AdminRoleName e " +
            "WHERE e.priority >= ?0 " +
            "ORDER by e.priority ASC, e.id ASC"
    )
    List<AdminRoleName> findByByPriorityGteOrderByPriorityAndId(
        int priorityGte
    );

    @EzyQuery(
        "SELECT e FROM AdminRoleName e " +
            "INNER JOIN AdminRole a ON e.id = a.roleId " +
            "WHERE a.adminId = ?0 " +
            "ORDER BY e.priority ASC"
    )
    Optional<AdminRoleName> findMinAdminRoleName(long adminId);

    @EzyQuery(
        "SELECT e FROM AdminRoleName e " +
            "WHERE e.id in ?0 " +
            "ORDER BY e.priority ASC"
    )
    Optional<AdminRoleName> findMinRoleByIds(
        Collection<Long> ids
    );

    long countByPriorityGte(int priorityGte);

    @EzyQuery(
        "SELECT e FROM AdminRoleName e " +
            "WHERE e.updatedAt > ?0 " +
            "OR (e.updatedAt = ?0 AND e.id > ?1) " +
            "ORDER BY e.updatedAt ASC, e.id ASC"
    )
    List<AdminRoleName> findAdminRoleNamesByUpdatedAtAndIdPaginationAsc(
        LocalDateTime updatedAtInclusive,
        long idExclusive,
        Next next
    );

    @EzyQuery(
        "SELECT e.updatedAt FROM AdminRoleName e " +
            "ORDER BY e.updatedAt ASC"
    )
    UpdatedAtValueResult findFirstUpdatedAt();
}
