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

import org.youngmonkeys.ezyplatform.model.WebLanguageModel;

import java.util.*;
import java.util.stream.Collectors;

public interface WebLanguageService {

    String getDefaultLanguageCode();

    WebLanguageModel[] getLanguages();

    default List<WebLanguageModel> getLanguageList() {
        return Arrays.asList(getLanguages());
    }

    default List<WebLanguageModel> getActiveLanguages() {
        return Arrays.stream(getLanguages())
            .filter(WebLanguageModel::isActive)
            .collect(Collectors.toList());
    }

    default WebLanguageModel getLanguageByCode(String code) {
        for (WebLanguageModel language : getLanguages()) {
            if (language.getCode().equals(code)) {
                return language;
            }
        }
        return null;
    }

    /**
     * Get web language map by code.
     *
     * @return language map by code.
     */
    default Map<String, WebLanguageModel> getLanguageMap() {
        return Arrays.stream(getLanguages())
            .collect(
                Collectors.toMap(
                    WebLanguageModel::getCode,
                    it -> it
                )
            );
    }

    default List<WebLanguageModel> getLanguagesByCodes(
        Collection<String> codes
    ) {
        Map<String, WebLanguageModel> languageMap =
            getLanguageMap();
        return codes
            .stream()
            .map(languageMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
