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
import org.youngmonkeys.ezyplatform.entity.DataI18nId;
import org.youngmonkeys.ezyplatform.model.DataI18nFieldModel;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;
import org.youngmonkeys.ezyplatform.repo.DataI18nRepository;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultData18nService implements Data18nService {

    private final DataI18nRepository dataI18nRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void saveData(DataI18nModel model) {
        dataI18nRepository.save(
            modelToEntityConverter.toEntity(model)
        );
    }

    @Override
    public void saveDataList(
        String dataType,
        long dataId,
        String language,
        Collection<DataI18nFieldModel> fields
    ) {
        dataI18nRepository.save(
            fields
                .stream()
                .map(it -> {
                    DataI18n entity = new DataI18n();
                    entity.setDataType(dataType);
                    entity.setDataId(dataId);
                    entity.setLanguage(language);
                    entity.setFieldName(it.getName());
                    entity.setFieldValue(it.getValue());
                    return entity;
                })
                .collect(Collectors.toList())
        );
    }

    @Override
    public String getDataI18nValueByTypeAndIdAndLanguageFieldName(
        String dataType,
        long dataId,
        String language,
        String fieldName
    ) {
        DataI18n entity = dataI18nRepository.findById(
            new DataI18nId(
                dataType,
                dataId,
                language,
                fieldName
            )
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
