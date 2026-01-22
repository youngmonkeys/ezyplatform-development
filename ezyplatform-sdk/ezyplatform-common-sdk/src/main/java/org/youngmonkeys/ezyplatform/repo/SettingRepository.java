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
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.result.TypeResult;

import java.time.LocalDateTime;
import java.util.List;

public interface SettingRepository extends EzyDatabaseRepository<Long, Setting> {

    void deleteByName(String name);

    @EzyQuery(
        "SELECT DISTINCT e.dataType " +
        "FROM Setting e " +
        "ORDER BY e.dataType ASC"
    )
    List<TypeResult> findAllDataTypes();

    Setting findByNameAndUpdatedAtGt(
        String name,
        LocalDateTime updatedAt
    );
}
