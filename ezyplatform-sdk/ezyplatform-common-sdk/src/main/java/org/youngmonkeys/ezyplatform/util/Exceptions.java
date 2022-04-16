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

package org.youngmonkeys.ezyplatform.util;

import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.traceStackToString;

public final class Exceptions {

    private Exceptions() {}

    public static String exceptionsToString(
        List<? extends Exception> exceptions
    ) {
        StringBuilder builder = new StringBuilder();
        for (Exception exception : exceptions) {
            builder.append(traceStackToString(exception));
        }
        return builder.toString();
    }

    public static String exceptionToSimpleString(Exception exception) {
        if (exception == null) {
            return "";
        } else {
            return exception.getClass().getName() + ": " + exception.getMessage();
        }
    }
}
