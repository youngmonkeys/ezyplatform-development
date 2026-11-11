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
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.DataI18n;
import org.youngmonkeys.ezyplatform.model.DataI18nFieldModel;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.repo.DataI18nRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@AllArgsConstructor
public class DefaultData18nService implements Data18nService {

    private final DataI18nRepository dataI18nRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void saveData(DataI18nModel model) {
        DataI18n entity = modelToEntityConverter.toEntity(model);
        DataI18n existing = dataI18nRepository
            .findByDataTypeAndDataIdAndLanguageAndFieldName(
                entity.getDataType(),
                entity.getDataId(),
                entity.getLanguage(),
                entity.getFieldName()
            );
        if (existing != null) {
            entity.setId(existing.getId());
        }
        dataI18nRepository.save(entity);
    }

    @Override
    public void saveDataList(
        String dataType,
        long dataId,
        String language,
        Collection<DataI18nFieldModel> fields
    ) {
        if (fields.isEmpty()) {
            return;
        }
        Map<String, Long> idByFieldName = dataI18nRepository
            .findByDataTypeAndDataIdAndLanguage(
                dataType,
                dataId,
                language
            )
            .stream()
            .collect(
                Collectors.toMap(
                    DataI18n::getFieldName,
                    DataI18n::getId,
                    (o, n) -> o
                )
            );
        Map<String, DataI18n> entityByFieldName = new LinkedHashMap<>();
        for (DataI18nFieldModel field : fields) {
            DataI18n entity = new DataI18n();
            entity.setId(
                idByFieldName.getOrDefault(field.getName(), ZERO_LONG)
            );
            entity.setDataType(dataType);
            entity.setDataId(dataId);
            entity.setLanguage(language);
            entity.setFieldName(field.getName());
            entity.setFieldValue(field.getValue());
            entityByFieldName.put(field.getName(), entity);
        }
        dataI18nRepository.save(
            new ArrayList<>(entityByFieldName.values())
        );
    }

    @Override
    public void deleteKeywordsByDataTypeAndDataId(
        String dataType,
        long dataId
    ) {
        dataI18nRepository.deleteByDataTypeAndDataId(
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
            dataI18nRepository.deleteByDataTypeAndDataIdIn(
                dataType,
                dataIds
            );
        }
    }

    @Override
    public String getDataI18nValueByTypeAndIdAndLanguageFieldName(
        String dataType,
        long dataId,
        String language,
        String fieldName
    ) {
        DataI18n entity = dataI18nRepository
            .findByDataTypeAndDataIdAndLanguageAndFieldName(
                dataType,
                dataId,
                language,
                fieldName
            );
        return entity != null ? entity.getFieldValue() : null;
    }

    @Override
    public Map<String, String> getDataI18nFieldsByTypeAndIdAndLanguage(
        String dataType,
        long dataId,
        String language
    ) {
        return dataI18nRepository
            .findByDataTypeAndDataIdAndLanguage(
                dataType,
                dataId,
                language
            )
            .stream()
            .filter(it -> it.getFieldValue() != null)
            .collect(
                Collectors.toMap(
                    DataI18n::getFieldName,
                    DataI18n::getFieldValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getDataI18nValueMapByTypeAndIdsAndLanguageAndFieldName(
        String dataType,
        Collection<Long> dataIds,
        String language,
        String fieldName
    ) {
        return dataI18nRepository
            .findByDataTypeAndDataIdInAndLanguageAndFieldName(
                dataType,
                dataIds,
                language,
                fieldName
            )
            .stream()
            .filter(it -> it.getFieldValue() != null)
            .collect(
                Collectors.toMap(
                    DataI18n::getDataId,
                    DataI18n::getFieldValue
                )
            );
    }

    @Override
    public Map<Long, Map<String, String>> getDataI18nFieldsMapByTypeAndIdsAndLanguage(
        String dataType,
        Collection<Long> dataIds,
        String language
    ) {
        return dataI18nRepository
            .findByDataTypeAndDataIdInAndLanguage(
                dataType,
                dataIds,
                language
            )
            .stream()
            .filter(it -> it.getFieldValue() != null)
            .collect(
                Collectors.groupingBy(
                    DataI18n::getDataId,
                    Collectors.mapping(
                        it -> it,
                        Collectors.toMap(
                            DataI18n::getFieldName,
                            DataI18n::getFieldValue,
                            (o, n) -> n
                        )
                    )
                )
            );
    }
}
