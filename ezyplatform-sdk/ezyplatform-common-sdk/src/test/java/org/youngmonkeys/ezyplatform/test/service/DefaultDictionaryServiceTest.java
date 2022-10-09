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

package org.youngmonkeys.ezyplatform.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.service.DefaultDictionaryService;
import org.youngmonkeys.ezyplatform.service.SettingService;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_EZYPLATFORM_DICTIONARY;

public class DefaultDictionaryServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SettingService settingService;

    @InjectMocks
    private DefaultDictionaryService sut;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void translateToAsciiStringTest() throws Exception {
        // given
        Map<Object, Object> dictionary = EzyMapBuilder
            .mapBuilder()
            .put("a", "d")
            .put("b", "e")
            .put("c", "f")
            .toMap();
        when(
            settingService.getCachedValue(
                SETTING_NAME_EZYPLATFORM_DICTIONARY,
                Collections.emptyMap()
            )
        ).thenReturn(dictionary);

        String input = "a b c ghKLM";

        // when
        String actual = sut.translateToStringInDashCase(
            input
        );

        // then
        Asserts.assertEquals(actual, "d-e-f-ghklm");

        verify(settingService, times(1)).getCachedValue(
            SETTING_NAME_EZYPLATFORM_DICTIONARY,
            Collections.emptyMap()
        );
        verify(objectMapper, times(0)).readValue(
            any(String.class),
            any(Class.class)
        );
    }
}
