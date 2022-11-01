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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.DataActionCount;
import org.youngmonkeys.ezyplatform.repo.DataActionCountRepository;
import org.youngmonkeys.ezyplatform.repo.DataActionCountTransactionalRepository;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultDataActionCountService implements DataActionCountService {

    private final ClockProxy clock;
    private final DataActionCountRepository dataActionCountRepository;
    private final DataActionCountTransactionalRepository
        dataActionCountTransactionalRepository;

    @Override
    public BigInteger increaseDataActionCount(
        String dataType,
        String actionType,
        long dataId,
        BigInteger value
    ) {
        return dataActionCountTransactionalRepository.increaseDataActionCount(
            dataType,
            actionType,
            dataId,
            value,
            clock.nowDateTime()
        );
    }

    @Override
    public BigInteger getDataActionCount(
        String dataType,
        String actionType,
        long dataId
    ) {
        DataActionCount entity = dataActionCountRepository
            .findByDataTypeAndActionTypeAndDataId(
                dataType,
                actionType,
                dataId
            );
        return entity == null ? BigInteger.ZERO : entity.getActionCount();
    }

    @Override
    public Map<Long, BigInteger> getDataActionCountMap(
        String dataType,
        String actionType,
        Collection<Long> dataIds
    ) {
        List<DataActionCount> entities = dataActionCountRepository
            .findByDataTypeAndActionTypeAndDataIdIn(
                dataType,
                actionType,
                dataIds
            );
        return entities
            .stream()
            .collect(
                Collectors.toMap(
                    DataActionCount::getDataId,
                    DataActionCount::getActionCount
                )
            );
    }
}
