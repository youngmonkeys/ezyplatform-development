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

package org.youngmonkeys.ezyplatform.service;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;
import org.youngmonkeys.ezyplatform.repo.DataRecordCountRepository;
import org.youngmonkeys.ezyplatform.repo.DataRecordCountTransactionalRepository;
import org.youngmonkeys.ezyplatform.result.CountResult;

@AllArgsConstructor
public class DefaultDataRecordCountService
    implements DataRecordCountService {

    private final DataRecordCountRepository dataRecordCountRepository;
    private final DataRecordCountTransactionalRepository
        dataRecordCountTransactionalRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void createDataRecordCountIfNotExists(String dataType) {
        DataRecordCount entity = dataRecordCountRepository
            .findById(dataType);
        if (entity == null) {
            entity = modelToEntityConverter.toNewDataRecordCount(
                dataType
            );
            dataRecordCountRepository.save(entity);
        }
    }

    @Override
    public void incrementRecordCount(String dataType, long value) {
        dataRecordCountTransactionalRepository.incrementRecordCount(
            dataType,
            value
        );
    }

    @Override
    public long getRecordCount(String dataType) {
        CountResult result = dataRecordCountRepository
            .findRecordCountByDataType(dataType);
        return result != null ? result.getCount() : 0L;
    }
}
