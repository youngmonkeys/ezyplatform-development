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
import org.youngmonkeys.ezyplatform.model.WebLanguageModel;

import java.util.Arrays;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_WEB_DEFAULT_LANGUAGE;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_WEB_LANGUAGES;

@AllArgsConstructor
public class DefaultWebLanguageService implements WebLanguageService {

    private final SettingService settingService;

    @Override
    public String getDefaultLanguageCode() {
        return settingService.getCachedValue(
            SETTING_NAME_WEB_DEFAULT_LANGUAGE
        );
    }

    @Override
    public WebLanguageModel[] getLanguages() {
        WebLanguageModel[] answer = settingService.getObjectValue(
            SETTING_NAME_WEB_LANGUAGES,
            WebLanguageModel.Mutable[].class
        );
        if (answer == null) {
            answer = new WebLanguageModel[0];
        }
        Arrays.sort(answer);
        return answer;
    }
}
