/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public final class JavascriptExecutors {

    private JavascriptExecutors() {}

    public static Object execute(
        String script,
        Map<String, Object> parameters
    ) {
        Context context = Context.enter();
        try {
            Scriptable scope = context.initStandardObjects();
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                scope.put(e.getKey(), scope, e.getValue());
            }
            return context.evaluateString(
                scope,
                script,
                null,
                0,
                null
            );
        } finally {
            Context.exit();
        }
    }

    public static BigDecimal execute(
        String script,
        Map<String, Object> parameters,
        int bigDecimalScale,
        RoundingMode roundingMode
    ) {
        Object result = execute(script, parameters);
        return result == null
            ? null
            : new BigDecimal(result.toString()).setScale(
            bigDecimalScale,
            roundingMode
        );
    }
}
