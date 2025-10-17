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

package org.youngmonkeys.devtools.repository;

import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreams;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.tvd12.ezyfox.io.EzyStrings.*;

/**
 * Generate repository interfaces from an entity class.
 * <p>
 * Guide to generating repository interfaces for an entity in your project.
 * </p>
 * <p>
 * Suppose your project is named <strong>accounting</strong> and your package is
 * <code>org.youngmonkeys.accounting</code>. You want to generate repository interfaces
 * for the <strong>AVoucher</strong> entity.
 * </p>
 * <p>
 * You will need to create a class named <code>AccountingRepositoryClassesGenerator</code>
 * in the following directory:
 * </p>
 * <pre>{@code
 * accounting/accounting-sdk/src/test/java/org/youngmonkeys/accounting/test
 * }</pre>
 * <p>
 * Example class structure:
 * </p>
 * <pre>{@code
 * package org.youngmonkeys.accounting.test;
 * import org.youngmonkeys.accounting.entity.AVoucher;
 * import org.youngmonkeys.devtools.repository.RepositoryClassesGenerator;
 *
 * public class AccountingRepositoryClassesGenerator {
 *     public static void main(String[] args) throws Exception {
 *         new RepositoryClassesGenerator(AVoucher.class)
 *             .generate();
 *     }
 * }
 * }</pre>
 * <p>
 * After running this class, the repository interfaces will be automatically
 * generated for the <code>AVoucher</code> entity.
 * </p>
 */
public class RepositoryClassesGenerator {

    private final String basePackageName;
    private final Class<?> customIdClass;
    private final String idClassName;
    private final String entityClassName;
    private final String entityVariableName;
    private final String tableName;
    private final String projectName;
    private final Path baseFolderPath;
    private final String adminIntegrationTestClassTemplate;
    private final String moduleRepositoryInterfaceTemplate;
    private final String sdkRepositoryClassTemplate;

    public RepositoryClassesGenerator(
        Class<?> entityClass
    ) throws Exception {
        String packageName = entityClass.getPackage().getName();
        this.basePackageName = packageName.substring(
            0,
            packageName.indexOf(".entity")
        );
        entityClassName = entityClass.getSimpleName();
        entityVariableName = entityClassName
            .substring(0, 1)
            .toLowerCase() + entityClassName.substring(1);
        IdClass idClassAnnotation = entityClass.getDeclaredAnnotation(
            IdClass.class
        );
        customIdClass = idClassAnnotation != null
            ? idClassAnnotation.value()
            : null;
        this.idClassName = extractIdClassName(entityClass);
        Table tableAnnotation = entityClass.getDeclaredAnnotation(
            Table.class
        );
        tableName = tableAnnotation != null
            ? tableAnnotation.name()
            : EMPTY_STRING;
        String currentFolderPath = new File("").getAbsolutePath();
        Path baseFolderPathTemp = Paths.get(currentFolderPath);
        if (currentFolderPath.endsWith("sdk")) {
            baseFolderPathTemp = baseFolderPathTemp.getParent();
        }
        baseFolderPath = baseFolderPathTemp;
        projectName = baseFolderPath.getFileName().toString();
        this.adminIntegrationTestClassTemplate = readTemplate(
            "admin_integration_test_class.txt"
        );
        this.moduleRepositoryInterfaceTemplate = readTemplate(
            "module_repo_class.txt"
        );
        this.sdkRepositoryClassTemplate = readTemplate(
            "sdk_repo_class.txt"
        );
    }

    private String extractIdClassName(Class<?> entityClass) {
        String idClassName = null;
        if (customIdClass != null) {
            idClassName = customIdClass.getSimpleName();
        } else {
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    Class<?> type = field.getType();
                    if (type == int.class) {
                        idClassName = "Integer";
                    } else if (type == long.class) {
                        idClassName = "Long";
                    } else {
                        idClassName = type.getSimpleName();
                    }
                    break;
                }
            }
        }
        if (idClassName == null) {
            throw new IllegalStateException(
                "entity: " + entityClassName + " has no id field"
            );
        }
        return idClassName;
    }

    private String readTemplate(
        String templateFileName
    ) throws IOException {
        EzyInputStreamLoader loader = new EzyAnywayInputStreamLoader();
        String filepath = "templates/repository/" + templateFileName;
        try (InputStream inputStream = loader.load(filepath)) {
            return EzyInputStreams.toStringUtf8(inputStream);
        }
    }

    public void generate() throws Exception {
        // sdk
        generateRepositoryClass(
            EMPTY_STRING,
            "sdk",
            "repo",
            entityClassName + "Repository",
            sdkRepositoryClassTemplate
        );

        // admin
        generateRepositoryClass(
            "Admin",
            "admin-plugin",
            "repo",
            "Admin" + entityClassName + "Repository",
            moduleRepositoryInterfaceTemplate
        );
        generateRepositoryClass(
            "Admin",
            "admin-plugin",
            "it.repo",
            "Admin" + entityClassName + "RepositoryIT",
            adminIntegrationTestClassTemplate
        );

        // web
        generateRepositoryClass(
            "Web",
            "web-plugin",
            "repo",
            "Web" + entityClassName + "Repository",
            moduleRepositoryInterfaceTemplate
        );
    }

    private void generateRepositoryClass(
        String moduleType,
        String moduleNameSuffix,
        String subPackageName,
        String className,
        String template
    ) throws IOException {
        String moduleNameFull = projectName + "-" + moduleNameSuffix;
        Path folderPath = baseFolderPath.resolve(moduleNameFull);
        if (!Files.exists(folderPath)) {
            System.out.println(
                "Module: " + moduleNameFull + " doest not exists"
            );
        }
        String mainFolder = "main";
        if (subPackageName.contains("it")
            && className.endsWith("IT")
        ) {
            mainFolder = "test";
        }
        Path sourcePath = folderPath.resolve(
            Paths.get("src", mainFolder, "java")
        );
        String packageName = basePackageName;
        if (isNotBlank(moduleType)) {
            packageName += "." + moduleType.toLowerCase();
        }
        packageName += "." + subPackageName;
        String packagePath = packageName.replace(".", "/");
        Path packageFolderPath = folderPath
            .resolve(sourcePath)
            .resolve(packagePath);
        if (!Files.exists(packageFolderPath)) {
            Files.createDirectories(packageFolderPath);
        }
        Path filePath = packageFolderPath
            .resolve(className + ".java");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        String moduleName = toDisplayName(projectName)
            .replace(" ", "");
        String content = generateContent(
            moduleType,
            template,
            moduleName
        );
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    }

    private String generateContent(
        String moduleType,
        String template,
        String moduleName
    ) {
        String importIdClass = customIdClass == null
            ? ""
            : "import " + customIdClass.getName() + ";\n";
        return template
            .replace("${basePackageName}", basePackageName)
            .replace("${idClassName}", idClassName)
            .replace("${entityClassName}", entityClassName)
            .replace("${tableName}", tableName)
            .replace("${moduleType}", moduleType)
            .replace("${moduleTypeLowercase}", moduleType.toLowerCase())
            .replace("${moduleName}", moduleName)
            .replace("${entityVariableName}", entityVariableName)
            .replace("${importIdClass}", importIdClass);
    }
}
