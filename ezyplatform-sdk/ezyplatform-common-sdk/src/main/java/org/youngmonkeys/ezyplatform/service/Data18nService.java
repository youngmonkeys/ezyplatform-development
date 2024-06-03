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

import org.youngmonkeys.ezyplatform.model.DataI18nFieldModel;
import org.youngmonkeys.ezyplatform.model.DataI18nModel;

import java.util.Collection;
import java.util.Map;

public interface Data18nService {

    void saveData(DataI18nModel model);

    void saveDataList(
        String dataType,
        long dataId,
        String language,
        Collection<DataI18nFieldModel> fields
    );

    void deleteKeywordsByDataTypeAndDataId(
        String dataType,
        long dataId
    );

    void deleteKeywordsByDataTypeAndDataIds(
        String dataType,
        Collection<Long> dataIds
    );

    String getDataI18nValueByTypeAndIdAndLanguageFieldName(
        String dataType,
        long dataId,
        String language,
        String fieldName
    );

    Map<String, String> getDataI18nFieldsByTypeAndIdAndLanguage(
        String dataType,
        long dataId,
        String language
    );

    Map<Long, String> getDataI18nValueMapByTypeAndIdsAndLanguageAndFieldName(
        String dataType,
        Collection<Long> dataIds,
        String language,
        String fieldName
    );

    Map<Long, Map<String, String>> getDataI18nFieldsMapByTypeAndIdsAndLanguage(
        String dataType,
        Collection<Long> dataIds,
        String language
    );
}
