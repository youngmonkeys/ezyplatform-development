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

package org.youngmonkeys.ezyplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.util.EzyLoggable;
import lombok.AllArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.mozilla.javascript.Context.jsToJava;
import static org.mozilla.javascript.ScriptableObject.getProperty;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;

public class JavascriptService extends EzyLoggable {

    private final EzyBeanContext beanContext;
    private final SettingService settingService;
    private final Map<String, String> jsBeanNameByJavaBeanName;

    public JavascriptService(
        EzyBeanContext beanContext,
        ObjectMapper objectMapper,
        SettingService settingService
    ) {
        this.beanContext = beanContext;
        this.settingService = settingService;
        this.jsBeanNameByJavaBeanName = new ConcurrentHashMap<>();
        settingService.addValueConverter(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            it -> objectMapper.readValue(
                it,
                Map.class
            )
        );
        settingService.watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
    }

    public void includeBeanName(
        String javaBeanName,
        String jsBeanName
    ) {
        jsBeanNameByJavaBeanName.put(
            javaBeanName,
            jsBeanName
        );
    }

    public Object execute(
        String script,
        Map<String, Object> parameters
    ) {
        return execute(
            script,
            parameters,
            getAllJsBeanNameByJavaBeanName()
        );
    }

    public Object execute(
        Map<String, Object> parameters,
        JavascriptFunction func
    ) {
        return execute(
            parameters,
            getAllJsBeanNameByJavaBeanName(),
            func
        );
    }

    public Object execute(
        String script,
        Map<String, Object> parameters,
        Map<String, String> jsBeanNameByJavaBeanName
    ) {
        return execute(
            parameters,
            jsBeanNameByJavaBeanName,
            new DefaultJavascriptFunction(script)
        );
    }

    public Object execute(
        Map<String, Object> parameters,
        Map<String, String> jsBeanNameByJavaBeanName,
        JavascriptFunction func
    ) {
        try (Context context = Context.enter()) {
            Scriptable scope = context.initStandardObjects();
            scope.put("console", scope, Console.getInstance());
            scope.put("logger", scope, logger);
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                scope.put(e.getKey(), scope, e.getValue());
            }
            scope.put(
                "properties",
                scope,
                beanContext.getProperties()
            );
            for (Map.Entry<String, String> e
                : jsBeanNameByJavaBeanName.entrySet()
            ) {
                Object bean = beanContext
                    .getBean(e.getKey(), Object.class);
                if (bean != null) {
                    ScriptableObject.putProperty(
                        scope,
                        e.getValue(),
                        Context.javaToJS(bean, scope)
                    );
                }
            }
            return func.run(context, scope);
        }
    }

    public Map<String, String> getAllJsBeanNameByJavaBeanName() {
        Map<String, String> beanNameMap = new HashMap<>();
        beanNameMap.putAll(jsBeanNameByJavaBeanName);
        beanNameMap.putAll(
            settingService.getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyMap()
            )
        );
        return beanNameMap;
    }

    public interface JavascriptFunction {

        Object run(Object context, Object scope);
    }

    public static class Console {

        private static final Console INSTANCE = new Console();

        private Console() {}

        public static Console getInstance() {
            return INSTANCE;
        }

        public void log(Object... args) {
            System.out.println(
                Stream.of(args)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "))
            );
        }
    }

    @AllArgsConstructor
    public static class DefaultJavascriptFunction
        implements JavascriptFunction {

        private final String script;

        @Override
        public Object run(Object context, Object scope) {
            if (isBlank(script)) {
                return NULL_STRING;
            }
            Object result = ((Context) context).evaluateString(
                (Scriptable) scope,
                script,
                NULL_STRING,
                ZERO,
                NULL_STRING
            );
            if (result == null || result instanceof Undefined) {
                return NULL_STRING;
            }
            return result.toString();
        }
    }

    @AllArgsConstructor
    public static class ObjectResultJavascriptFunction
        implements JavascriptFunction {

        private final String script;

        @Override
        public Object run(Object context, Object scope) {
            if (isBlank(script)) {
                return null;
            }
            Object result = ((Context) context).evaluateString(
                (Scriptable) scope,
                script,
                NULL_STRING,
                ZERO,
                NULL_STRING
            );
            if (result == null || result instanceof Undefined) {
                return null;
            }
            if (result instanceof NativeObject) {
                return toMap((NativeObject) result);
            }
            if (result instanceof NativeArray) {
                return toList((NativeArray) result);
            }
            return jsToJava(result, Object.class);
        }

        public static Map<String, Object> toMap(NativeObject obj) {
            Map<String, Object> map = new LinkedHashMap<>();

            for (Object id : obj.getIds()) {
                String key = String.valueOf(id);
                Object value = obj.get(key, obj);
                map.put(key, unwrap(value));
            }
            return map;
        }

        public static List<Object> toList(NativeArray arr) {
            List<Object> list = new ArrayList<>();

            long len = arr.getLength();
            for (int i = 0; i < len; i++) {
                Object value = getProperty(arr, i);
                list.add(unwrap(value));
            }
            return list;
        }

        private static Object unwrap(Object value) {
            if (value == null || value instanceof Undefined) {
                return null;
            }
            if (value instanceof NativeObject) {
                return toMap((NativeObject) value);
            }
            if (value instanceof NativeArray) {
                return toList((NativeArray) value);
            }
            if (value instanceof Number
                || value instanceof Boolean
                || value instanceof String
            ) {
                return value;
            }
            return jsToJava(value, Object.class);
        }
    }
}
