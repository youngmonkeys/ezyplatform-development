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

    void deleteByAdminId(
        long adminId
    );

    void deleteByAdminIdIn(
        Collection<Long> adminIds
    );

    void deleteByMetaKey(String metaKey);

    void deleteByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    void deleteByAdminIdInAndMetaKeyIn(
        Collection<Long> adminIds,
        Collection<String> metaKeys
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.metaKey = ?0 " +
            "AND e.metaValue = ?1 " +
            "ORDER BY e.id DESC"
    )
    Optional<AdminMeta> findByMetaKeyAndMetaValue(
        String metaKey,
        String metaValue
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId = ?0 " +
            "AND e.metaKey = ?1 " +
            "ORDER BY e.id ASC"
    )
    Optional<AdminMeta> findByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId = ?0 " +
            "AND e.metaKey = ?1 " +
            "ORDER BY e.id ASC"
    )
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

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId = ?0 " +
            "AND e.metaKey = ?1 " +
            "AND e.metaValue = ?2 " +
            "ORDER BY e.id DESC"
    )
    Optional<AdminMeta> findByAdminIdAndMetaKeyAndMetaValue(
        long adminId,
        String metaKey,
        String metaValue
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.metaKey = ?0 " +
            "AND e.metaValue IN ?1 " +
            "ORDER BY e.id ASC"
    )
    List<AdminMeta> findByMetaKeyAndMetaValueIn(
        String metaKey,
        Collection<String> metaValues
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId IN ?0 " +
            "AND e.metaKey = ?1 " +
            "ORDER BY e.id ASC"
    )
    List<AdminMeta> findByAdminIdInAndMetaKey(
        Collection<Long> adminIds,
        String metaKey
    );

    @EzyQuery(
        "SELECT e FROM AdminMeta e " +
            "WHERE e.adminId = ?0 " +
            "AND e.metaKey IN ?1 " +
            "ORDER BY e.id ASC"
    )
    List<AdminMeta> findByAdminIdAndMetaKeyIn(
        long adminId,
        Collection<String> metaKeys
    );
}
