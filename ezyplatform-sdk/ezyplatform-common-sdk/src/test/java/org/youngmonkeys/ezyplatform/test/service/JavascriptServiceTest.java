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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.test.assertion.Asserts;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
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
    private ObjectMapper objectMapper;
    private SettingService settingService;

    @BeforeMethod
    public void setup() {
        this.beanContext = mock(EzyBeanContext.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.settingService = mock(SettingService.class);
    }

    @Test
    public void constructorTest() {
        // when
        new JavascriptService(beanContext, objectMapper, settingService);

        // then
        verify(settingService, times(1)).addValueConverter(
            eq(SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES),
            any()
        );
        verify(settingService, times(1)).watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
        verifyNoMoreInteractions(settingService);
        verifyZeroInteractions(beanContext, objectMapper);
    }

    @Test
    public void executeWithBeanNamesFromSettingTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("siteName", "EzyPlatform");
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        doReturn(Collections.singletonMap("greetingBean", "greeter"))
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Codex");
        parameters.put("value", 7);

        // when
        Object actual = instance.execute(
            "greeter.greet(name) + '-' + properties.get('siteName') + '-' + (value + 1)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "Hello Codex-EzyPlatform-8");
        verify(settingService, times(1)).getCachedValue(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            Collections.emptyMap()
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
        when(beanContext.getBean("missingBean", Object.class))
            .thenReturn(null);
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("a", 3);
        parameters.put("b", 9);
        Map<String, String> jsBeanNameByJavaBeanName = new LinkedHashMap<>();
        jsBeanNameByJavaBeanName.put("sumBean", "calculator");
        jsBeanNameByJavaBeanName.put("missingBean", "missing");

        // when
        Object actual = instance.execute(
            "calculator.sum(a, b)",
            parameters,
            jsBeanNameByJavaBeanName
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
    public void executeWithJavascriptFunctionAndBeanNamesFromSettingTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("siteName", "EzyPlatform");
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        doReturn(Collections.singletonMap("greetingBean", "greeter"))
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Platform");
        parameters.put("number", 5);

        // when
        Object actual = instance.execute(
            parameters,
            (context, scope) -> ((Context) context).evaluateString(
                (Scriptable) scope,
                "greeter.greet(name) + '-' + properties.get('siteName') + '-' + (number * 2)",
                "JavascriptServiceTest",
                1,
                null
            ).toString()
        );

        // then
        Asserts.assertEquals(actual, "Hello Platform-EzyPlatform-10");
        verify(settingService, times(1)).getCachedValue(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            Collections.emptyMap()
        );
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
    }

    @Test
    public void executeWithIncludedBeanNameTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        when(beanContext.getBean("sumBean", Object.class))
            .thenReturn(new SumBean());
        doReturn(Collections.singletonMap("greetingBean", "greeter"))
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );
        instance.includeBeanName("sumBean", "calculator");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Codex");
        parameters.put("a", 2);
        parameters.put("b", 3);

        // when
        Object actual = instance.execute(
            "greeter.greet(name) + '-' + calculator.sum(a, b)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "Hello Codex-5");
        verify(settingService, times(1)).getCachedValue(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            Collections.emptyMap()
        );
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
        verify(beanContext, times(1)).getBean("sumBean", Object.class);
    }

    @Test
    public void getAllJsBeanNameByJavaBeanNameTest() {
        // given
        Map<String, String> cachedBeanNames = new HashMap<>();
        cachedBeanNames.put("greetingBean", "greeter");
        cachedBeanNames.put("sumBean", "settingCalculator");
        doReturn(cachedBeanNames)
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );
        instance.includeBeanName("sumBean", "calculator");
        instance.includeBeanName("extraBean", "extra");

        // when
        Map<String, String> actual =
            instance.getAllJsBeanNameByJavaBeanName();

        // then
        Asserts.assertEquals(actual.size(), 3);
        Asserts.assertEquals(actual.get("greetingBean"), "greeter");
        Asserts.assertEquals(actual.get("sumBean"), "settingCalculator");
        Asserts.assertEquals(actual.get("extraBean"), "extra");
    }

    @Test
    public void executeUndefinedResultTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        doReturn(Collections.emptyMap())
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
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

    @Test
    public void executeNullResultTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        doReturn(Collections.emptyMap())
            .when(settingService)
            .getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            );
        JavascriptService instance = new JavascriptService(
            beanContext,
            objectMapper,
            settingService
        );

        // when
        Object actual = instance.execute(
            "null",
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
