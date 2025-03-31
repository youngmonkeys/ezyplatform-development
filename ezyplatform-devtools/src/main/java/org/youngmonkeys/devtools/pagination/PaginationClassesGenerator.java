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

package org.youngmonkeys.devtools.pagination;

import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreams;

import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.tvd12.ezyfox.io.EzyStrings.*;

/**
 * Generate pagination classes from an entity class.
 * <p>
 * Guide to generating pagination classes for an entity in your project.
 * </p>
 * <p>
 * Suppose your project is named <strong>accounting</strong> and your package is
 * <code>org.youngmonkeys.accounting</code>. You want to generate pagination classes
 * for the <strong>AVoucher</strong> entity.
 * </p>
 * <p>
 * You will need to create a class named <code>AccountingPaginationClassesGenerator</code>
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
 * import org.youngmonkeys.devtools.pagination.PaginationClassesGenerator;
 *
 * public class AccountingPaginationClassesGenerator {
 *     public static void main(String[] args) throws Exception {
 *         new PaginationClassesGenerator(AVoucher.class)
 *             .generate();
 *     }
 * }
 * }</pre>
 * <p>
 * After running this class, the pagination classes will be automatically
 * generated for the <code>AVoucher</code> entity.
 * </p>
 */
public class PaginationClassesGenerator {

    private final String basePackageName;
    private final String entityClassName;
    private final String entityVariableName;
    private final String tableName;
    private final String projectName;
    private final Path baseFolderPath;
    private final String adminIntegrationTestClassTemplate;
    private final String defaultFilterClassTemplate;
    private final String filterInterfaceTemplate;
    private final String idAscPaginationParameterClass;
    private final String idDescPaginationParameterClass;
    private final String modulePaginationParameterConverterClassTemplate;
    private final String modulePaginationRepoClassTemplate;
    private final String modulePaginationServiceClassTemplate;
    private final String paginationParameterInterfaceTemplate;
    private final String paginationParameterConverterClassTemplate;
    private final String paginationSortOrderClassTemplate;
    private final String sdkPaginationRepoClassTemplate;
    private final String sdkPaginationServiceClassTemplate;

    public PaginationClassesGenerator(
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
            .toUpperCase() + entityClassName.substring(1);
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
        this.defaultFilterClassTemplate = readTemplate(
            "default_filter_class.txt"
        );
        this.filterInterfaceTemplate = readTemplate(
            "filter_interface.txt"
        );
        this.idAscPaginationParameterClass = readTemplate(
            "id_asc_pagination_parameter_class.txt"
        );
        this.idDescPaginationParameterClass = readTemplate(
            "id_desc_pagination_parameter_class.txt"
        );
        this.modulePaginationParameterConverterClassTemplate = readTemplate(
            "module_pagination_parameter_converter_class.txt"
        );
        this.modulePaginationRepoClassTemplate = readTemplate(
            "module_pagination_repo_class.txt"
        );
        this.modulePaginationServiceClassTemplate = readTemplate(
            "module_pagination_service_class.txt"
        );
        this.paginationParameterInterfaceTemplate = readTemplate(
            "pagination_parameter_interface.txt"
        );
        this.paginationParameterConverterClassTemplate = readTemplate(
            "pagination_paramter_converter_class.txt"
        );
        this.paginationSortOrderClassTemplate = readTemplate(
            "pagination_sort_order_class.txt"
        );
        this.sdkPaginationRepoClassTemplate = readTemplate(
            "sdk_pagination_repo_class.txt"
        );
        this.sdkPaginationServiceClassTemplate = readTemplate(
            "sdk_pagination_service_class.txt"
        );
    }

    private String readTemplate(
        String templateFileName
    ) throws IOException {
        EzyInputStreamLoader loader = new EzyAnywayInputStreamLoader();
        String filepath = "templates/pagination/" + templateFileName;
        try (InputStream inputStream = loader.load(filepath)) {
            return EzyInputStreams.toStringUtf8(inputStream);
        }
    }

    public void generate() throws Exception {
        // sdk
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            entityClassName + "Filter",
            filterInterfaceTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            "Default" + entityClassName + "Filter",
            defaultFilterClassTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            entityClassName + "PaginationParameter",
            paginationParameterInterfaceTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            entityClassName + "PaginationParameterConverter",
            paginationParameterConverterClassTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            entityClassName + "PaginationSortOrder",
            paginationSortOrderClassTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            "IdAsc" + entityClassName + "PaginationParameter",
            idAscPaginationParameterClass
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "pagination",
            "IdDesc" + entityClassName + "PaginationParameter",
            idDescPaginationParameterClass
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "repo",
            "Pagination" + entityClassName + "Repository",
            sdkPaginationRepoClassTemplate
        );
        generatePaginationClass(
            EMPTY_STRING,
            "sdk",
            "service",
            "Pagination" + entityClassName + "Service",
            sdkPaginationServiceClassTemplate
        );

        // admin
        generatePaginationClass(
            "Admin",
            "admin-plugin",
            "pagination",
            "Admin" + entityClassName + "PaginationParameterConverter",
            modulePaginationParameterConverterClassTemplate
        );
        generatePaginationClass(
            "Admin",
            "admin-plugin",
            "repo",
            "AdminPagination" + entityClassName + "Repository",
            modulePaginationRepoClassTemplate
        );
        generatePaginationClass(
            "Admin",
            "admin-plugin",
            "service",
            "AdminPagination" + entityClassName + "Service",
            modulePaginationServiceClassTemplate
        );
        generatePaginationClass(
            "Admin",
            "admin-plugin",
            "it.service",
            "AdminPagination" + entityClassName + "ServiceIT",
            adminIntegrationTestClassTemplate
        );

        // web
        generatePaginationClass(
            "Web",
            "web-plugin",
            "pagination",
            "Web" + entityClassName + "PaginationParameterConverter",
            modulePaginationParameterConverterClassTemplate
        );
        generatePaginationClass(
            "Web",
            "web-plugin",
            "repo",
            "WebPagination" + entityClassName + "Repository",
            modulePaginationRepoClassTemplate
        );
        generatePaginationClass(
            "Web",
            "web-plugin",
            "service",
            "WebPagination" + entityClassName + "Service",
            modulePaginationServiceClassTemplate
        );
    }

    private void generatePaginationClass(
        String moduleType,
        String moduleNameSuffix,
        String subPackageName,
        String className,
        String template
    ) throws IOException {
        String moduleName = toDisplayName(projectName)
            .replace(" ", "");
        String content = template
            .replace("${basePackageName}", basePackageName)
            .replace("${entityClassName}", entityClassName)
            .replace("${entityVariableName}", entityVariableName)
            .replace("${tableName}", tableName)
            .replace("${moduleType}", moduleType)
            .replace("${moduleTypeLowercase}", moduleType.toLowerCase())
            .replace("${moduleName}", moduleName);
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
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    }
}
