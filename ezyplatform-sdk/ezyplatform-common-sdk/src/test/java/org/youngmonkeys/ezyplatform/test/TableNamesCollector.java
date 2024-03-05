/*
 * Copyright 2024 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test;

import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyfox.reflect.EzyReflectionProxy;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TableNamesCollector {

    public static void main(String[] args) {
        EzyReflection reflection = new EzyReflectionProxy(
            "org.youngmonkeys.ezyplatform.entity"
        );
        Set<Class<?>> classes = reflection.getAnnotatedClasses(
            Table.class
        );
        List<String> lines = new ArrayList<>();
        for (Class<?> clazz : classes) {
            Table ann = clazz.getDeclaredAnnotation(Table.class);
            String varName = ann.name().substring("ezy_".length()).toUpperCase();
            if (varName.endsWith("S")) {
                varName = varName.substring(0, varName.length() - 1);
            }
            lines.add(
                "public static final String TABLE_NAME_" + varName +
                    " = \"" + ann.name() + "\";"
            );
        }
        Collections.sort(lines);
        System.out.println(String.join("\n", lines));
    }
}
