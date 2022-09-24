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

import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class StringConverters {

    private StringConverters() {}

    public static String trimOrNull(String str) {
        return isBlank(str) ? null : str.trim();
    }

    public static String toNotNull(String str) {
        return str != null ? str : EMPTY_STRING;
    }

    public static BigInteger stringToBigInteger(String value) {
        return value == null ? null : new BigInteger(value);
    }

    @SuppressWarnings("unchecked")
    public static Set<String> stringToSetString(String value) {
        if (isBlank(value)) {
            return Collections.emptySet();
        }
        try {
            return SingletonStringDeserializer
                .getInstance()
                .deserialize(value, Set.class, String.class);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }
}
