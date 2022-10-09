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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_EZYPLATFORM_DICTIONARY;

public class DefaultDictionaryService implements DictionaryService {

    private final SettingService settingService;

    public DefaultDictionaryService(
        ObjectMapper objectMapper,
        SettingService settingService,
        Object ...components
    ) {
        this.settingService = settingService;
        this.init(settingService, components);
        settingService.addValueConverter(
            SETTING_NAME_EZYPLATFORM_DICTIONARY,
            value -> objectMapper.readValue(
                value,
                new TypeReference<Map<String, String>>() {}
            )
        );
        settingService.watchLastUpdatedTimeAndCache(
            SETTING_NAME_EZYPLATFORM_DICTIONARY
        );
    }

    protected void init(
        SettingService settingService,
        Object ...components
    ) {}

    @Override
    public String translate(String input) {
        Map<String, String> dictionary = settingService.getCachedValue(
            SETTING_NAME_EZYPLATFORM_DICTIONARY,
            Collections.emptyMap()
        );
        return dictionary.get(input);
    }

    @Override
    public String translateToAsciiString(String input) {
        Map<String, String> dictionary = settingService.getCachedValue(
            SETTING_NAME_EZYPLATFORM_DICTIONARY,
            Collections.emptyMap()
        );
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            String ch = String.valueOf(input.charAt(i));
            builder.append(dictionary.getOrDefault(ch, ch));
        }
        return builder.toString();
    }
}
