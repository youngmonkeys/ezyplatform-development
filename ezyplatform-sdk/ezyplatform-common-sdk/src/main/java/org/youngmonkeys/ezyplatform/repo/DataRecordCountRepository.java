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
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.result.CountResult;
import org.youngmonkeys.ezyplatform.result.IdResult;

public interface DataRecordCountRepository
    extends EzyDatabaseRepository<String, DataRecordCount> {

    @EzyQuery(
        "SELECT e.lastRecordId FROM DataRecordCount e " +
            "WHERE e.dataType = ?0"
    )
    IdResult findLastRecordIdByDataType(String dataType);

    @EzyQuery(
        "SELECT e.recordCount FROM DataRecordCount e " +
            "WHERE e.dataType = ?0"
    )
    CountResult findRecordCountByDataType(String dataType);
}
