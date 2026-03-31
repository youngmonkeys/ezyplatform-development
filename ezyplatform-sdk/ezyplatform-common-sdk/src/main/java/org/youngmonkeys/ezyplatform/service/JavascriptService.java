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

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import lombok.AllArgsConstructor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tvd12.ezyfox.io.EzySets.combine;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public class JavascriptService {

    private final EzyBeanContext beanContext;
    private final SettingService settingService;
    private final Set<String> beanNames;

    public JavascriptService(
        EzyBeanContext beanContext,
        SettingService settingService
    ) {
        this.beanContext = beanContext;
        this.settingService = settingService;
        this.beanNames = ConcurrentHashMap.newKeySet();
        settingService.addValueConverter(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
            it -> SingletonStringDeserializer
                .getInstance()
                .deserialize(it, List.class, String.class)
        );
        settingService.watchLastUpdatedTimeAndCache(
            SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES
        );
    }

    public void addBeanName(String beanName) {
        beanNames.add(beanName);
    }

    @SuppressWarnings("unchecked")
    public Object execute(
        String script,
        Map<String, Object> parameters
    ) {
        return execute(
            script,
            parameters,
            combine(
                beanNames,
                settingService.getCachedValue(
                    SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                    Collections.emptyList()
                )
            )
        );
    }

    @SuppressWarnings("unchecked")
    public Object execute(
        Map<String, Object> parameters,
        JavascriptFunction func
    ) {
        return execute(
            parameters,
            combine(
                beanNames,
                settingService.getCachedValue(
                    SETTING_NAME_JAVASCRIPT_SERVICE_BEAN_NAMES,
                    Collections.emptyList()
                )
            ),
            func
        );
    }

    public Object execute(
        String script,
        Map<String, Object> parameters,
        Collection<String> beanNames
    ) {
        return execute(
            parameters,
            beanNames,
            new DefaultJavascriptFunction(script)
        );
    }

    public Object execute(
        Map<String, Object> parameters,
        Collection<String> beanNames,
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
            for (String beanName : beanNames) {
                Object bean = beanContext
                    .getBean(beanName, Object.class);
                if (bean != null) {
                    ScriptableObject.putProperty(
                        scope,
                        beanName,
                        Context.javaToJS(bean, scope)
                    );
                }
            }
            return func.run(context, scope);
        }
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
