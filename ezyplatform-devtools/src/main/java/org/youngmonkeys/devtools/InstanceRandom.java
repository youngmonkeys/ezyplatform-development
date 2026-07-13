/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.devtools;

import com.tvd12.ezyfox.tool.EzyObjectInstanceRandom;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class InstanceRandom extends EzyObjectInstanceRandom {

    public <T> T randomObject(Class<T> clazz) {
        return randomObject(clazz, true);
    }

    public <T> T randomInstanceValues(T instance) {
        return randomInstanceValues(instance, true);
    }

    public <T> T randomInstanceValues(
        T instance,
        boolean includeAllFields
    ) {
        return randomObjectValues(
            instance,
            includeAllFields
        );
    }

    @Override
    protected Map<Class<?>, Supplier<Object>> defaultValueRandoms() {
        Map<Class<?>, Supplier<Object>> randoms =
            super.defaultValueRandoms();
        randoms.put(Integer.TYPE, () -> {
            Random random = new Random();
            return Math.abs(random.nextInt());
        });
        randoms.put(Long.TYPE, () -> {
            Random random = new Random();
            return Math.abs(random.nextInt());
        });
        randoms.put(String.class, () -> {
            Random random = new Random();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 8; ++i) {
                int index = random.nextInt(DEFAULT_STRINGS.length);
                builder.append(DEFAULT_STRINGS[index]);
            }
            return builder.toString();
        });
        return randoms;
    }
}
