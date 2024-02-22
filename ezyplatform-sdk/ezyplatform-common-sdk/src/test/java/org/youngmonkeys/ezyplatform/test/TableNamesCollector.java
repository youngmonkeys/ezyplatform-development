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
