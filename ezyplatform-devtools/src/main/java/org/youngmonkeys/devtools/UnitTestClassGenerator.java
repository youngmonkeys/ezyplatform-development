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

import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyField;
import com.tvd12.ezyfox.reflect.EzyFields;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzySets.newHashSet;
import static com.tvd12.ezyfox.reflect.EzyClasses.getVariableName;

public class UnitTestClassGenerator {

    private final String classSimpleName;
    private final List<EzyField> components;
    private boolean hasConstructor;
    private final List<String> imports = new ArrayList<>();
    private final List<String> content = new ArrayList<>();

    public UnitTestClassGenerator(Class<?> javaClass) {
        EzyClass clazz = new EzyClass(javaClass);
        this.classSimpleName = javaClass.getSimpleName();
        Constructor<?> constructor = clazz
            .getDeclaredConstructors()
            .stream()
            .max(Comparator.comparingInt(Constructor::getParameterCount))
            .orElse(null);
        if (constructor != null && constructor.getParameterCount() > 0) {
            hasConstructor = true;
            components = Stream
                .of(constructor.getParameterTypes())
                .map(it ->
                    EzyField.builder()
                        .clazz(it)
                        .fieldName(getVariableName(it))
                        .build()
                )
                .collect(Collectors.toList());
        } else {
            components = newArrayList(
                EzyFields.getFields(javaClass),
                EzyField::new
            );
        }
    }

    public void printContent() {
        System.out.println(generateContent());
    }

    @SuppressWarnings({"unchecked", "MethodLength"})
    public String generateContent() {
        imports.addAll(
            Arrays.asList(
                "org.testng.annotations.*;",
                "static org.mockito.Mockito.*;"
            )
        );
        imports.add(InstanceRandom.class.getName() + ";");
        Set<Class<?>> componentClasses = newHashSet(
            components,
            EzyField::getType
        );
        for (Class<?> componentClass : componentClasses) {
            imports.add(componentClass.getName() + ";");
        }
        content.add(
            imports
                .stream()
                .distinct()
                .map(it -> "import " + it)
                .collect(Collectors.joining("\n"))
        );
        content.add("");

        // setup
        content.add("public class " + classSimpleName + "Test {");
        for (EzyField component : components) {
            content.add(
                tabs(1) + "private " +
                component.getType().getSimpleName() + " " +
                component.getName() + ";"
            );
        }
        content.add(tabs(1) + "private " + classSimpleName + " instance;");
        content.add("");
        content.add(
            tabs(1) + "private final InstanceRandom instanceRandom ="
        );
        content.add(tabs(2) + "new InstanceRandom();");
        content.add("");
        content.add(tabs(1) + "@BeforeMethod");
        content.add(tabs(1) + "public void setup() {");
        for (EzyField component : components) {
            content.add(
                tabs(2) + component.getName() +
                    " = mock(" + component.getType().getSimpleName() +
                    ".class);"
            );
        }
        if (hasConstructor) {
            content.add(
                tabs(2) + "instance = new " +
                    classSimpleName + "("
            );
            List<String> params = new ArrayList<>();
            for (Class<?> componentClass : componentClasses) {
                params.add(
                    tabs(3) + getVariableName(componentClass)
                );
            }
            content.add(String.join(",\n", params));
            content.add(tabs(2) + ");");
        } else {
            content.add(
                tabs(2) + "instance = new " +
                    classSimpleName + "();"
            );
            List<String> setters = new ArrayList<>();
            for (EzyField component : components) {
                setters.add(
                    tabs(2) + "instance." +
                        component.getSetterMethod() +
                        "(" + component.getName() + ");"
                );
            }
            content.add(String.join("\n", setters));
        }
        content.add(tabs(1) + "}");

        // verify all
        if (!components.isEmpty()) {
            content.add("");
            content.add(tabs(1) + "@AfterMethod");
            content.add(tabs(1) + "public void verifyAll() {");
            content.add(tabs(2) + "verifyNoMoreInteractions(");
            List<String> params = new ArrayList<>();
            for (EzyField field : components) {
                params.add(
                    tabs(3) + field.getName()
                );
            }
            content.add(String.join(",\n", params));
            content.add(tabs(2) + ");");
            content.add(tabs(1) + "}");
        }

        content.add("}");
        return String.join("\n", content);
    }

    private static String tabs(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            builder.append("    ");
        }
        return builder.toString();
    }
}
