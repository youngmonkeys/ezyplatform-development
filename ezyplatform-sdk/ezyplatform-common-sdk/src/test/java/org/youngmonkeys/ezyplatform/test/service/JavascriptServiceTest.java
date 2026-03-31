/*
 * Copyright 2026 youngmonkeys.org
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

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.service.JavascriptService;
import org.youngmonkeys.ezyplatform.service.SettingService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES;

public class JavascriptServiceTest {

    private EzyBeanContext beanContext;
    private SettingService settingService;

    @BeforeMethod
    public void setup() {
        this.beanContext = mock(EzyBeanContext.class);
        this.settingService = mock(SettingService.class);
    }

    @Test
    public void constructorTest() {
        // when
        new JavascriptService(beanContext, settingService);

        // then
        verify(settingService, times(1)).addValueConverter(
            eq(SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES),
            any()
        );
        verify(settingService, times(1)).watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
        verifyNoMoreInteractions(settingService);
        verifyNoMoreInteractions(beanContext);
    }

    @Test
    public void executeWithBeanNamesFromSettingTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("siteName", "EzyPlatform");
        GreetingBean greetingBean = new GreetingBean();
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(greetingBean);
        when(
            settingService.getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyList()
            )
        )
            .thenReturn(Collections.singletonList("greetingBean"));
        JavascriptService instance = new JavascriptService(
            beanContext,
            settingService
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Codex");
        parameters.put("value", 7);

        // when
        Object actual = instance.execute(
            "greetingBean.greet(name) + '-' + properties.get('siteName') + '-' + (value + 1)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "Hello Codex-EzyPlatform-8");
        verify(settingService, times(1)).addValueConverter(
            eq(SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES),
            any()
        );
        verify(settingService, times(1)).watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
        verify(settingService, times(1)).getCachedValue(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            Collections.emptyList()
        );
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
    }

    @Test
    public void executeWithProvidedBeanNamesTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("unused", "value");
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("sumBean", Object.class))
            .thenReturn(new SumBean());
        JavascriptService instance = new JavascriptService(
            beanContext,
            settingService
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("a", 3);
        parameters.put("b", 9);
        Collection<String> beanNames = Arrays.asList("sumBean", "missingBean");

        // when
        Object actual = instance.execute(
            "sumBean.sum(a, b)",
            parameters,
            beanNames
        );

        // then
        Asserts.assertEquals(actual, "12");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("sumBean", Object.class);
        verify(beanContext, times(1)).getBean("missingBean", Object.class);
        verify(settingService, times(1)).addValueConverter(
            eq(SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES),
            any()
        );
        verify(settingService, times(1)).watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
        verifyNoMoreInteractions(settingService);
    }

    @Test
    public void executeUndefinedResultTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(
            settingService.getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyList()
            )
        )
            .thenReturn(Collections.emptyList());
        JavascriptService instance = new JavascriptService(
            beanContext,
            settingService
        );

        // when
        Object actual = instance.execute(
            "var answer = 1 + 1;",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(actual, NULL_STRING);
    }

    public static class GreetingBean {

        public String greet(String name) {
            return "Hello " + name;
        }
    }

    public static class SumBean {

        public int sum(int a, int b) {
            return a + b;
        }
    }
}
