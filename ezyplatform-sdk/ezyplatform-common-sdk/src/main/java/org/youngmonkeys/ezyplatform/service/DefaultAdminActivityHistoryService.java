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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.AdminActivityHistory;
import org.youngmonkeys.ezyplatform.model.AddAdminActivityHistoryModel;

@AllArgsConstructor
public class DefaultAdminActivityHistoryService implements AdminActivityHistoryService {

    private final EzyDatabaseRepository<Long, AdminActivityHistory> adminActivityHistoryRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void addHistory(AddAdminActivityHistoryModel model) {
        adminActivityHistoryRepository.save(
            modelToEntityConverter.toEntity(model)
        );
    }
}
