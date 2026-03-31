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
import lombok.AllArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public class JavascriptService {

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String, String> getAllJsBeanNameByJavaBeanName() {
        Map<String, String> beanNameMap = new HashMap<>();
        beanNameMap.putAll(jsBeanNameByJavaBeanName);
        beanNameMap.putAll(
            (Map) settingService.getCachedValue(
                SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                Collections.emptyList()
            )
        );
        return beanNameMap;
    }

    public interface JavascriptFunction {

        Object run(Object context, Object scope);
    }

    @AllArgsConstructor
    public static class DefaultJavascriptFunction
        implements JavascriptFunction {

        private final String script;

        @Override
        public Object run(Object context, Object scope) {
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
}
