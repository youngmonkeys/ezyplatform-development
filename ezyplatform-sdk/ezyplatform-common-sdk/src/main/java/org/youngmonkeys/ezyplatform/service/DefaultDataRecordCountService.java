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
import org.youngmonkeys.ezyplatform.result.CountResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@AllArgsConstructor
public class DefaultDataRecordCountService
    implements DataRecordCountService {

    private final DataRecordCountRepository dataRecordCountRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void createDataRecordCountIfNotExists(
        String dataType
    ) {
        createDataRecordCountIfNotExistsAndGetEntity(
            dataType
        );
    }

    private DataRecordCount createDataRecordCountIfNotExistsAndGetEntity(
        String dataType
    ) {
        DataRecordCount entity = dataRecordCountRepository
            .findByDataType(dataType);
        if (entity == null) {
            entity = modelToEntityConverter.toNewDataRecordCount(
                dataType
            );
            dataRecordCountRepository.save(entity);
        }
        return entity;
    }

    @Override
    public void createDataRecordCountByDataNameAndRecordTypeIfNotExists(
        String dataName,
        String recordType
    ) {
        createDataRecordCountByDataNameAndRecordTypeIfNotExistsAndGetEntity(
            dataName,
            recordType
        );
    }

    public DataRecordCount createDataRecordCountByDataNameAndRecordTypeIfNotExistsAndGetEntity(
        String dataName,
        String recordType
    ) {
        DataRecordCount entity = dataRecordCountRepository
            .findByDataNameAndRecordType(dataName, recordType);
        if (entity == null) {
            entity = modelToEntityConverter.toNewDataRecordCount(
                toDataType(dataName, recordType)
            );
            entity.setDataName(dataName);
            entity.setRecordType(recordType);
            dataRecordCountRepository.save(entity);
        }
        return entity;
    }

    @Override
    public void incrementRecordCount(
        String dataType,
        long value
    ) {
        createDataRecordCountIfNotExists(dataType);
        dataRecordCountRepository.updateRecordCountByDataType(
            dataType,
            value
        );
    }

    @Override
    public long incrementRecordCountAndGet(
        String dataType,
        long value
    ) {
        DataRecordCount entity =
            createDataRecordCountIfNotExistsAndGetEntity(
                dataType
            );
        dataRecordCountRepository.updateRecordCountByDataType(
            dataType,
            value
        );
        return entity.getRecordCount() + value;
    }

    @Override
    public void incrementRecordCountByDataNameAndRecordType(
        String dataName,
        String recordType,
        long value
    ) {
        createDataRecordCountByDataNameAndRecordTypeIfNotExists(
            dataName,
            recordType
        );
        dataRecordCountRepository.updateRecordCountByDataNameAndRecordType(
            dataName,
            recordType,
            value
        );
    }

    @Override
    public long incrementRecordCountByDataNameAndRecordTypeAndGet(
        String dataName,
        String recordType,
        long value
    ) {
        DataRecordCount entity =
            createDataRecordCountByDataNameAndRecordTypeIfNotExistsAndGetEntity(
                dataName,
                recordType
            );
        dataRecordCountRepository.updateRecordCountByDataNameAndRecordType(
            dataName,
            recordType,
            value
        );
        return entity.getRecordCount() + value;
    }

    @Override
    public long getRecordCount(String dataType) {
        CountResult result = dataRecordCountRepository
            .findRecordCountByDataType(dataType);
        return result != null ? result.getCount() : ZERO_LONG;
    }

    @Override
    public long getRecordCountByDataNameAndRecordType(
        String dataName,
        String recordType
    ) {
        CountResult result = dataRecordCountRepository
            .findRecordCountByDataNameAndRecordType(
                dataName,
                recordType
            );
        return result != null ? result.getCount() : ZERO_LONG;
    }

    @Override
    public Map<String, Long> getRecordCountMapByDataNameAndRecordTypes(
        String dataName,
        Collection<String> recordTypes
    ) {
        if (recordTypes.isEmpty()) {
            return Collections.emptyMap();
        }
        return dataRecordCountRepository
            .findByDataNameAndRecordTypeIn(
                dataName,
                recordTypes
            )
            .stream()
            .collect(
                Collectors.toMap(
                    DataRecordCount::getRecordType,
                    DataRecordCount::getRecordCount
                )
            );
    }
}
