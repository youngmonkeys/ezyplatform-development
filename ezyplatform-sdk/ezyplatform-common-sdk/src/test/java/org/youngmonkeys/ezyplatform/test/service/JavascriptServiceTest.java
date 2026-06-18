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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.service.JavascriptService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.service.JavascriptService.ObjectResultJavascriptFunction;

public class JavascriptServiceTest {

    private EzyBeanContext beanContext;

    @BeforeMethod
    public void setup() {
        this.beanContext = mock(EzyBeanContext.class);
    }

    @Test
    public void constructorTest() {
        // when
        new JavascriptService(beanContext);

        // then
        verifyZeroInteractions(beanContext);
    }

    @Test
    public void executeWithBeanContextTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("siteName", "EzyPlatform");
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Codex");
        parameters.put("value", 7);

        // when
        Object actual = instance.execute(
            "" +
                "var greeter = getBean('greetingBean');" +
                "greeter.greet(name) + '-' + properties.get('siteName') + '-' + (value + 1)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "Hello Codex-EzyPlatform-8");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
    }

    @Test
    public void executeWithJavascriptFunctionAndBeanContextTest() {
        // given
        Properties properties = new Properties();
        properties.setProperty("siteName", "EzyPlatform");
        when(beanContext.getProperties()).thenReturn(properties);
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Platform");
        parameters.put("number", 5);

        // when
        Object actual = instance.execute(
            parameters,
            (context, scope) -> ((Context) context).evaluateString(
                (Scriptable) scope,
                "" +
                    "var greeter = getBean('greetingBean');" +
                    "greeter.greet(name) + '-' + properties.get('siteName') + '-' + (number * 2)",
                "JavascriptServiceTest",
                1,
                null
            ).toString()
        );

        // then
        Asserts.assertEquals(actual, "Hello Platform-EzyPlatform-10");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
    }

    @Test
    public void executeWithMultipleBeansFromBeanContextTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("greetingBean", Object.class))
            .thenReturn(new GreetingBean());
        when(beanContext.getBean("sumBean", Object.class))
            .thenReturn(new SumBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Codex");
        parameters.put("a", 2);
        parameters.put("b", 3);

        // when
        Object actual = instance.execute(
            "" +
                "var greeter = getBean('greetingBean');" +
                "var calculator = getBean('sumBean');" +
                "greeter.greet(name) + '-' + calculator.sum(a, b)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "Hello Codex-5");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("greetingBean", Object.class);
        verify(beanContext, times(1)).getBean("sumBean", Object.class);
    }

    @Test
    public void executeWithIncludedBeanNameTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("sumBean", Object.class))
            .thenReturn(new SumBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        instance.includeBeanName("sumBean", "calculator");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("a", 3);
        parameters.put("b", 9);

        // when
        Object actual = instance.execute(
            "calculator.sum(a, b)",
            parameters
        );

        // then
        Asserts.assertEquals(actual, "12");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("sumBean", Object.class);
    }

    @Test
    public void executeWithIncludedMissingBeanNameTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("missingBean", Object.class))
            .thenReturn(null);
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        instance.includeBeanName("missingBean", "missing");

        // when
        Object actual = instance.execute(
            "typeof missing",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(actual, "undefined");
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1)).getBean("missingBean", Object.class);
    }

    @Test
    public void executeUndefinedResultTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
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
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            "null",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(actual, NULL_STRING);
    }

    @Test
    public void executeWithObjectResultJavascriptFunctionTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("characterBean", Object.class))
            .thenReturn(new CharacterBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "EzyPlatform");
        parameters.put("enabled", true);

        // when
        Object actual = instance.execute(
            parameters,
            new ObjectResultJavascriptFunction(
                "" +
                    "var characters = getBean('characterBean');" +
                    "({" +
                    "mapValue: {id: 1, name: name}," +
                    "listValue: ['admin', 'web']," +
                    "stringValue: name," +
                    "numberValue: 21 * 2," +
                    "booleanValue: enabled," +
                    "characterValue: characters.first(name)" +
                "})"
            )
        );

        // then
        Asserts.assertTrue(actual instanceof Map);
        Map<?, ?> map = (Map<?, ?>) actual;
        Map<?, ?> mapValue = (Map<?, ?>) map.get("mapValue");
        Asserts.assertEquals(
            ((Number) mapValue.get("id")).intValue(),
            1
        );
        Asserts.assertEquals(
            mapValue.get("name"),
            "EzyPlatform"
        );
        List<?> listValue = (List<?>) map.get("listValue");
        Asserts.assertEquals(listValue.size(), 2);
        Asserts.assertEquals(listValue.get(0), "admin");
        Asserts.assertEquals(listValue.get(1), "web");
        Asserts.assertEquals(
            map.get("stringValue"),
            "EzyPlatform"
        );
        Asserts.assertEquals(
            ((Number) map.get("numberValue")).intValue(),
            42
        );
        Asserts.assertEquals(
            map.get("booleanValue"),
            Boolean.TRUE
        );
        Asserts.assertEquals(
            map.get("characterValue"),
            Character.valueOf('E')
        );
        verify(beanContext, times(1)).getProperties();
        verify(beanContext, times(1))
            .getBean("characterBean", Object.class);
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithMapTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            Collections.emptyMap(),
            new ObjectResultJavascriptFunction(
                "({" +
                    "id: 1," +
                    "name: 'EzyPlatform'," +
                    "active: true," +
                    "tags: ['admin', 'web']," +
                    "owner: {name: 'youngmonkeys'}" +
                "})"
            )
        );

        // then
        Asserts.assertTrue(actual instanceof Map);
        Map<?, ?> map = (Map<?, ?>) actual;
        Asserts.assertEquals(
            ((Number) map.get("id")).intValue(),
            1
        );
        Asserts.assertEquals(map.get("name"), "EzyPlatform");
        Asserts.assertEquals(map.get("active"), Boolean.TRUE);
        List<?> tags = (List<?>) map.get("tags");
        Asserts.assertEquals(tags.size(), 2);
        Asserts.assertEquals(tags.get(0), "admin");
        Asserts.assertEquals(tags.get(1), "web");
        Asserts.assertEquals(
            ((Map<?, ?>) map.get("owner")).get("name"),
            "youngmonkeys"
        );
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithListTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            Collections.emptyMap(),
            new ObjectResultJavascriptFunction(
                "[" +
                    "{code: 'media', enabled: true}," +
                    "{code: 'delivery', values: [1, 2, 3]}" +
                "]"
            )
        );

        // then
        Asserts.assertTrue(actual instanceof List);
        List<?> list = (List<?>) actual;
        Asserts.assertEquals(list.size(), 2);
        Asserts.assertEquals(
            ((Map<?, ?>) list.get(0)).get("code"),
            "media"
        );
        Asserts.assertEquals(
            ((Map<?, ?>) list.get(0)).get("enabled"),
            Boolean.TRUE
        );
        List<?> values = (List<?>) ((Map<?, ?>) list.get(1))
            .get("values");
        Asserts.assertEquals(
            ((Number) values.get(2)).intValue(),
            3
        );
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithStringTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            Collections.emptyMap(),
            new ObjectResultJavascriptFunction("'EzyPlatform'")
        );

        // then
        Asserts.assertEquals(actual, "EzyPlatform");
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithNumberTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            Collections.emptyMap(),
            new ObjectResultJavascriptFunction("21 * 2")
        );

        // then
        Asserts.assertTrue(actual instanceof Number);
        Asserts.assertEquals(((Number) actual).intValue(), 42);
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithBooleanTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        JavascriptService instance = new JavascriptService(
            beanContext
        );

        // when
        Object actual = instance.execute(
            Collections.emptyMap(),
            new ObjectResultJavascriptFunction("1 < 2")
        );

        // then
        Asserts.assertEquals(actual, Boolean.TRUE);
    }

    @Test
    public void executeObjectResultJavascriptFunctionWithCharacterTest() {
        // given
        when(beanContext.getProperties()).thenReturn(new Properties());
        when(beanContext.getBean("characterBean", Object.class))
            .thenReturn(new CharacterBean());
        JavascriptService instance = new JavascriptService(
            beanContext
        );
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("value", "Codex");

        // when
        Object actual = instance.execute(
            parameters,
            new ObjectResultJavascriptFunction(
                "" +
                    "var characters = getBean('characterBean');" +
                    "characters.first(value)"
            )
        );

        // then
        Asserts.assertEquals(actual, Character.valueOf('C'));
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

    public static class CharacterBean {

        public Character first(String value) {
            return value.charAt(0);
        }
    }
}
