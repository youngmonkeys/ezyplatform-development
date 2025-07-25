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
import org.youngmonkeys.ezyplatform.entity.AdminMeta;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AdminMetaRepository
    extends EzyDatabaseRepository<Long, AdminMeta> {

    void deleteByAdminId(long adminId);

    void deleteByAdminIdIn(Collection<Long> adminIds);

    List<AdminMeta> findByAdminId(long adminId);

    Optional<AdminMeta> findByMetaKeyAndMetaValue(
        String metaKey,
        String metaValue
    );

    Optional<AdminMeta> findByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    List<AdminMeta> findByAdminIdAndMetaKey(
        long adminId,
        String metaKey,
        Next next
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId = ?0 AND e.metaKey = ?1 " +
            "ORDER BY e.id DESC"
    )
    Optional<AdminMeta> findByAdminIdAndMetaKeyOrderByIdDesc(
        long adminId,
        String metaKey
    );

    Optional<AdminMeta> findByAdminIdAndMetaKeyAndMetaValue(
        long adminId,
        String metaKey,
        String metaValue
    );

    List<AdminMeta> findByMetaKeyAndMetaValueIn(
        String metaKey,
        Collection<String> metaValues
    );

    List<AdminMeta> findByAdminIdInAndMetaKey(
        Collection<Long> adminIds,
        String metaKey
    );

    List<AdminMeta> findByAdminIdAndMetaKeyIn(
        long adminId,
        Collection<String> metaKeys
    );
}
