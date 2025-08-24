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
import org.youngmonkeys.ezyplatform.entity.UniqueData;
import org.youngmonkeys.ezyplatform.entity.UniqueDataId;

import java.util.Collection;
import java.util.List;

public interface UniqueDataRepository
    extends EzyDatabaseRepository<UniqueDataId, UniqueData> {

    List<UniqueData> findByDataTypeAndDataIdIn(
        String dataType,
        Collection<Long> dataIds
    );

    List<UniqueData> findByDataTypeAndDataIdInAndUniqueKey(
        String dataType,
        Collection<Long> dataIds,
        String uniqueKey
    );

    List<UniqueData> findByDataTypeAndDataIdAndUniqueKeyIn(
        String dataType,
        long dataId,
        Collection<String> uniqueKeys
    );

    List<UniqueData> findByDataTypeAndDataIdInAndUniqueKeyIn(
        String dataType,
        Collection<Long> dataIds,
        Collection<String> uniqueKeys
    );
}
