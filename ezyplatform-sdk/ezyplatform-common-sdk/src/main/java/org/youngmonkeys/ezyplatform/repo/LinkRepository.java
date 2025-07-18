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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import com.tvd12.ezyfox.util.Next;
import org.youngmonkeys.ezyplatform.entity.Link;

import java.util.List;

public interface LinkRepository extends EzyDatabaseRepository<Long, Link> {
    
    void deleteByLinkUri(String linkUri);

    @EzyQuery(
        "SELECT e FROM Link e " +
            "WHERE e.id > ?0 " +
            "ORDER BY e.id ASC"
    )
    List<Link> findByIdGt(long idExclusive, Next next);

    @EzyQuery(
        "SELECT e FROM Link e WHERE e.linkType = ?0 ORDER BY e.id DESC"
    )
    List<Link> findByLinkTypeOrderByIdDesc(
        String linkType,
        Next next
    );
}
