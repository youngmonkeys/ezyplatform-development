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

package org.youngmonkeys.ezyplatform.util;

import com.tvd12.ezyfox.util.EzyEquals;
import com.tvd12.ezyfox.util.EzyHashCodes;
import lombok.Getter;

import java.util.function.Function;

public class ProxyObject<T> {
    @Getter
    private final T value;
    private final EzyEquals<T> equals;
    private final EzyHashCodes hashCodes;

    public ProxyObject(T value) {
        this.value = value;
        this.equals = new EzyEquals<>();
        this.hashCodes = new EzyHashCodes();
    }

    public ProxyObject<T> equalFieldValueExtractor(
        Function<T, Object> fieldExtractor
    ) {
        equals.function(fieldExtractor);
        hashCodes.append(fieldExtractor.apply(value));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof ProxyObject) {
            isEqual = equals.isEquals(
                value,
                ((ProxyObject<?>) obj).value
            );
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return hashCodes.toHashCode();
    }
}
