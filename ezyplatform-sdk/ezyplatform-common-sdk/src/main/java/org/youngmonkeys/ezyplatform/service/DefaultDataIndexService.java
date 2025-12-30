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

import com.tvd12.ezyfox.util.EzyLoggable;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataIndex;
import org.youngmonkeys.ezyplatform.model.SaveDataKeywordModel;
import org.youngmonkeys.ezyplatform.repo.DataIndexRepository;

import java.util.Collection;

@AllArgsConstructor
public class DefaultDataIndexService
        extends EzyLoggable
        implements DataIndexService {

    private final DataIndexRepository dataIndexRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void saveKeyword(
        String dataType,
        SaveDataKeywordModel model
    ) {
        long dataId = model.getDataId();
        String keyword = model.getKeyword();
        DataIndex entity = dataIndexRepository
            .findByDataTypeAndDataIdAndKeyword(
                dataType,
                dataId,
                keyword
            );
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(dataType, model);
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        try {
            dataIndexRepository.save(entity);
        } catch (Exception e) {
            logger.info(
                "save keyword: {} of data type: {} and id: {} failed",
                keyword,
                dataType,
                dataId
            );
        }
    }

    @Override
    public void saveKeywords(
        String dataType,
        Collection<SaveDataKeywordModel> dataKeywords
    ) {
        for (SaveDataKeywordModel model : dataKeywords) {
            saveKeyword(dataType, model);
        }
    }

    @Override
    public void deleteKeywordsByDataTypeAndDataId(
        String dataType,
        long dataId
    ) {
        dataIndexRepository.deleteByDataTypeAndDataId(
            dataType,
            dataId
        );
    }

    @Override
    public void deleteKeywordsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    ) {
        if (!dataIds.isEmpty()) {
            dataIndexRepository.deleteByDataTypeAndDataIdIn(
                dataType,
                dataIds
            );
        }
    }

    @Override
    public boolean containsDataIndex(
        String dataType,
        long dataId,
        String keyword
    ) {
        return dataIndexRepository.findByDataTypeAndDataIdAndKeyword(
            dataType,
            dataId,
            keyword
        ) != null;
    }
}
