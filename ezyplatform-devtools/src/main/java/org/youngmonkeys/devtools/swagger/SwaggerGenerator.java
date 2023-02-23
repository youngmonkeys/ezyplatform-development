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

package org.youngmonkeys.devtools.swagger;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.file.EzySimpleFileWriter;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.*;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyStrings.*;

@AllArgsConstructor
public class SwaggerGenerator {

    private final String packageToScan;

    public String generate() throws IOException {
        SwaggerTemplate swaggerTemplate = new SwaggerTemplate();
        String servers = swaggerTemplate.createContent(
            "server",
            EzyMapBuilder
                .mapBuilder()
                .put("url", "http://localhost:8080")
                .put("description", "localhost")
                .toMap(),
            1
        );
        EzyReflection reflection = new EzyReflectionProxy(
            packageToScan
        );
        Set<Class<?>> controllerClasses = reflection.getAnnotatedClasses(
            Controller.class
        );
        List<ApiClass> apiClasses = newArrayList(
            controllerClasses,
            this::extractControllerClass
        );
        List<String> apis = new ArrayList<>();
        for (ApiClass apiClass : apiClasses) {
            for (String uri : apiClass.methodsByUri.keySet()) {
                List<ApiMethod> apiMethods = apiClass.methodsByUri.get(uri);
                for (int i = 0; i < apiMethods.size(); ++i) {
                    ApiMethod apiMethod = apiMethods.get(i);
                    apis.add(
                        swaggerTemplate.createContent(
                            "api",
                            EzyMapBuilder
                                .mapBuilder()
                                .put("uri", i == 0 ? uri : "")
                                .put("method", apiMethod.type)
                                .put("summary", createApiSummary(apiMethod))
                                .put("description", apiMethod.name)
                                .put(
                                    "parameters",
                                    createApiParameters(
                                        apiMethod.parameters,
                                        swaggerTemplate
                                    )
                                )
                                .put(
                                    "request_body",
                                    createApiRequestBody(
                                        apiMethod,
                                        swaggerTemplate
                                    )
                                )
                                .put(
                                    "responses",
                                    createApiResponseBody(
                                        apiMethod,
                                        swaggerTemplate
                                    )
                                )
                                .toMap(),
                            1
                        )
                    );
                }
            }
        }
        return swaggerTemplate.createContent(
            "file",
            EzyMapBuilder
                .mapBuilder()
                .put("open_api_version", "3.0.2")
                .put("title", "APIs")
                .put("version", "1.0.0")
                .put("servers", servers)
                .put("apis", joinString(apis, 1))
                .toMap(),
            0
        );
    }

    public void generateToDefaultFile() throws IOException {
        generateToFile(
            "swagger.yaml"
        );
    }

    public void generateToFile(String filePath) throws IOException {
        File file = new File(filePath);
        EzyFileUtil.createFileIfNotExists(file);
        new EzySimpleFileWriter().write(
            file,
            generate(),
            StandardCharsets.UTF_8.toString()
        );
        System.out.println("generated to: " + filePath);
    }

    private String createApiSummary(ApiMethod apiMethod) {
        List<String> lines = new ArrayList<>();
        lines.add("- feature: " + apiMethod.feature);
        lines.add("- api: " + (apiMethod.api ? "yes" : "no"));
        lines.add("- authenticated: " + (apiMethod.authenticated ? "yes" : "no"));
        return joinString(lines, 4);
    }

    private String createApiParameters(
        List<ApiParameter> apiParameters,
        SwaggerTemplate swaggerTemplate
    ) {
        if (apiParameters.isEmpty()) {
            return EMPTY_STRING;
        }
        List<String> lines = new ArrayList<>();
        lines.add("parameters:");
        lines.addAll(
            apiParameters
                .stream()
                .map(it -> createApiParameter(it, swaggerTemplate))
                .collect(Collectors.toList())
        );
        return joinString(lines, 4);
    }

    private String createApiParameter(
        ApiParameter apiParameter,
        SwaggerTemplate swaggerTemplate
    ) {
        return swaggerTemplate.createContent(
            "parameter",
            EzyMapBuilder
                .mapBuilder()
                .put("name", apiParameter.name)
                .put("in", apiParameter.in)
                .put("type", mapJavaType(apiParameter.javaType))
                .put("required", "true")
                .put(
                    "description",
                    apiParameter.defaultValue != null
                        ? "default value is " + apiParameter.defaultValue
                        : "parameter"
                )
                .toMap(),
            4
        );
    }

    private String createApiRequestBody(
        ApiMethod apiMethod,
        SwaggerTemplate swaggerTemplate
    ) {
        if (apiMethod.requestBody == null) {
            return "";
        }
        return swaggerTemplate.createContent(
            "request body",
            EzyMapBuilder
                .mapBuilder()
                .put("content_type", "application/json")
                .put("required", "true")
                .put("type", "object")
                .put(
                    "properties",
                    createProperties(
                        apiMethod.requestBody.fields,
                        null,
                        8,
                        "set"
                    )
                )
                .toMap(),
            3
        );
    }

    private String createApiResponseBody(
        ApiMethod apiMethod,
        SwaggerTemplate swaggerTemplate
    ) {
        String responseContent;
        if (apiMethod.response.fields == null) {
            responseContent = EMPTY_STRING;
        } else {
            String type = Collection.class.isAssignableFrom(
                apiMethod.response.type
            ) ? "array" : "object";
            String responseData;
            if ("object".equals(type)) {
                responseData = createProperties(
                    apiMethod.response.fields,
                    apiMethod.response.genericsType,
                    9,
                    "get"
                );
            } else {
                responseData = swaggerTemplate.createContent(
                    "response array data",
                    EzyMapBuilder
                        .mapBuilder()
                        .put("type", "array")
                        .put(
                            "properties",
                            createProperties(
                                extractApiDataFields(
                                    apiMethod.response.genericsType,
                                    "get"
                                ),
                                apiMethod.response.genericsType,
                                10,
                                "get"
                            )
                        )
                        .toMap(),
                    8
                );
            }
            responseContent = swaggerTemplate.createContent(
                "response content",
                EzyMapBuilder
                    .mapBuilder()
                    .put("content_type", "application/json")
                    .put("type", type)
                    .put("response_data", responseData)
                    .toMap(),
                6
            );
        }
        String code = "200";
        String description = "success";
        if (apiMethod.response.type == Redirect.class) {
            code = "302";
            description = "redirect";
        } else if (apiMethod.response.type == ResponseEntity.class) {
            code = "204";
            description = "no content";
        } else if (apiMethod.response.type == View.class) {
            description = "return a view";
        }
        return swaggerTemplate.createContent(
            "response",
            EzyMapBuilder
                .mapBuilder()
                .put("code", code)
                .put("description", description)
                .put("content", responseContent)
                .toMap(),
            4
        );
    }

    private String createProperties(
        List<ApiDataField> fields,
        Class<?> parentGenericTypes,
        int doubleSpaces,
        String methodPrefix
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("properties:");
        for (ApiDataField field : fields) {
            lines.add(field.name + ":");
            String type = mapJavaType(field.javaType);
            lines.add("  type: " + type);
            if ("array".equals(type)) {
                lines.add("  items:");
                Class<?> genericsType = getGenericType(
                    field.javaGenericsType
                );
                if (genericsType == null) {
                    genericsType = parentGenericTypes;
                }
                if (genericsType == null) {
                    genericsType = field.javaType.getComponentType();
                }
                String genericsTypeName;
                if (genericsType == null) {
                    genericsTypeName = "unknown";
                } else if (EzyTypes.NON_ARRAY_TYPES.contains(genericsType)) {
                    genericsTypeName = genericsType.getSimpleName().toLowerCase();
                } else if (Collection.class.isAssignableFrom(genericsType)) {
                    genericsTypeName = "array";
                } else {
                    genericsTypeName = "object";
                }
                lines.add("    type: " + genericsTypeName);
                if ("object".equals(genericsTypeName)) {
                    String line = "    " + createProperties(
                        extractApiDataFields(
                            genericsType,
                            methodPrefix
                        ),
                        null,
                        doubleSpaces + 3,
                        methodPrefix
                    );
                    lines.add(line);
                }
            } else if ("object".equals(type)) {
                String line = "  " + createProperties(
                    extractApiDataFields(
                        field.javaType,
                        methodPrefix
                    ),
                    null,
                    doubleSpaces + 2,
                    methodPrefix
                );
                lines.add(line);
            }
        }
        return joinString(lines, doubleSpaces);
    }

    private ApiClass extractControllerClass(
        Class<?> controllerClass
    ) {
        Api apiAnnotation = controllerClass.getAnnotation(
            Api.class
        );
        Controller controllerAnnotation = controllerClass.getAnnotation(
            Controller.class
        );
        Authenticated authenticatedAnnotation = controllerClass.getAnnotation(
            Authenticated.class
        );
        EzyFeature featureAnnotation = controllerClass.getAnnotation(
            EzyFeature.class
        );
        String uri = controllerAnnotation.uri();
        if (isBlank(uri)) {
            uri = controllerAnnotation.value();
        }
        if (isBlank(uri)) {
            uri = "/";
        }
        String feature = featureAnnotation == null
            ? ""
            : featureAnnotation.value();
        final String finalUri = uri;
        List<ApiMethod> methods = EzyMethods
            .getMethods(controllerClass)
            .stream()
            .filter(it ->
                it.isAnnotationPresent(DoDelete.class) ||
                    it.isAnnotationPresent(DoGet.class) ||
                    it.isAnnotationPresent(DoPost.class) ||
                    it.isAnnotationPresent(DoPut.class)
            )
            .map(it -> extractControllerMethod(
                    it,
                    apiAnnotation != null,
                    authenticatedAnnotation != null,
                    finalUri,
                    feature
                )
            )
            .collect(Collectors.toList());
        return new ApiClass(
            methods
                .stream()
                .collect(
                    Collectors.groupingBy(
                        it -> it.uri,
                        Collectors.toList()
                    )
                )
        );
    }

    private ApiMethod extractControllerMethod(
        Method method,
        boolean parentIsApi,
        boolean parentIsAuthenticated,
        String parentUri,
        String parentFeature
    ) {
        DoDelete doDelete = method.getAnnotation(DoDelete.class);
        DoGet doGet = method.getAnnotation(DoGet.class);
        DoPost doPost = method.getAnnotation(DoPost.class);
        DoPut doPut = method.getAnnotation(DoPut.class);
        String type;
        String uri;
        if (doDelete != null) {
            type = "delete";
            uri = doDelete.uri();
            if (isBlank(uri)) {
                uri = doDelete.value();
            }
        } else if (doGet != null) {
            type = "get";
            uri = doGet.uri();
            if (isBlank(uri)) {
                uri = doGet.value();
            }
        } else if (doPost != null) {
            type = "post";
            uri = doPost.uri();
            if (isBlank(uri)) {
                uri = doPost.value();
            }
        } else {
            type = "put";
            uri = doPut.uri();
            if (isBlank(uri)) {
                uri = doPut.value();
            }
        }
        String fullUri = parentUri;
        if (isNotBlank(uri)) {
            fullUri = (parentUri + uri).replace("//", "/");
        }
        EzyFeature feature = method.getAnnotation(EzyFeature.class);
        Api api = method.getAnnotation(Api.class);
        Authenticated authenticated = method.getAnnotation(Authenticated.class);
        return new ApiMethod(
            method.getName(),
            type,
            fullUri,
            feature != null ? feature.value() : parentFeature,
            authenticated != null || parentIsAuthenticated,
            api != null || parentIsApi,
            extractParameters(fullUri, method),
            extractRequestBody(method),
            extractResponse(method)
        );
    }

    private List<ApiParameter> extractParameters(
        String uri,
        Method method
    ) {
        List<String> pathVariables = new ArrayList<>();
        String uriRemain = uri;
        while (true) {
            int index = uriRemain.indexOf('{');
            if (index <= 0) {
                break;
            }
            uriRemain = uriRemain.substring(index + 1);
            index = uriRemain.indexOf('}');
            if (index <= 0) {
                break;
            }
            pathVariables.add(uriRemain.substring(0, index));
            if (index >= uriRemain.length() - 1) {
                break;
            }
            uriRemain = uriRemain.substring(index + 1);
        }
        int pathVariableIndex = 0;
        List<ApiParameter> answer = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            String name = null;
            String defaultValue = null;
            String in = null;
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                in = "path";
                name = pathVariable.value();
                if (isBlank(name)) {
                    name = pathVariables.get(pathVariableIndex);
                }
                ++pathVariableIndex;
            }
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                in = "query";
                name = requestParam.value();
                if (isBlank(name)) {
                    name = parameter.getName();
                }
                defaultValue = requestParam.defaultValue();
            }
            if (in != null) {
                answer.add(
                    new ApiParameter(
                        in,
                        name,
                        parameter.getType(),
                        defaultValue
                    )
                );
            }
        }
        return answer;
    }

    private ApiRequestBody extractRequestBody(Method method) {
        Class<?> requestBodyParamType = null;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                requestBodyParamType = parameter.getType();
                break;
            }
        }
        if (requestBodyParamType == null) {
            return null;
        }
        List<ApiDataField> fields = extractApiDataFields(
            requestBodyParamType,
            "set"
        );
        return new ApiRequestBody(fields);
    }

    private List<ApiDataField> extractApiDataFields(
        Class<?> clazz,
        String methodPrefix
    ) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        boolean isSetter = "set".equals(methodPrefix);
        return EzyMethods.getPublicMethods(clazz)
            .stream()
            .filter(
                it -> (it.getName().startsWith(methodPrefix)
                    || it.getName().startsWith("is"))
                    && (isSetter
                    ? it.getParameterCount() > 0
                    : it.getParameterCount() == 0
                )
            )
            .map(it ->
                new ApiDataField(
                    isSetter
                        ? EzyMethods.getFieldNameOfSetter(it)
                        : EzyMethods.getFieldNameOfGetter(it),
                    isSetter
                        ? it.getParameterTypes()[0]
                        : it.getReturnType(),
                    isSetter
                        ? it.getGenericParameterTypes()[0]
                        : it.getGenericReturnType()
                )
            )
            .sorted(Comparator.comparing(a -> a.name))
            .collect(Collectors.toList());
    }

    private ApiResponse extractResponse(Method method) {
        List<ApiDataField> fields = null;
        Class<?> responseBodyType = method.getReturnType();
        if (responseBodyType != void.class
            && responseBodyType != Redirect.class
            && responseBodyType != ResponseEntity.class
            && responseBodyType != View.class
        ) {
            fields = extractApiDataFields(
                responseBodyType,
                "get"
            );
        }
        return new ApiResponse(
            method.getReturnType(),
            getGenericType(method.getGenericReturnType()),
            fields
        );
    }

    private Class<?> getGenericType(Type type) {
        try {
            return EzyGenerics.getOneGenericClassArgument(type);
        } catch (Exception e) {
            return null;
        }
    }


    @AllArgsConstructor
    public static class ApiClass {
        private final Map<String, List<ApiMethod>> methodsByUri;
    }

    @AllArgsConstructor
    public static class ApiMethod {
        private final String name;
        private final String type;
        private final String uri;
        private final String feature;
        private final boolean api;
        private final boolean authenticated;
        private final List<ApiParameter> parameters;
        private final ApiRequestBody requestBody;
        private final ApiResponse response;
    }

    @AllArgsConstructor
    private static class ApiRequestBody {
        private final List<ApiDataField> fields;
    }

    @AllArgsConstructor
    private static class ApiResponse {
        private final Class<?> type;
        private final Class<?> genericsType;
        private final List<ApiDataField> fields;
    }

    @AllArgsConstructor
    private static class ApiDataField {
        private final String name;
        private final Class<?> javaType;
        private final Type javaGenericsType;
    }

    @AllArgsConstructor
    private static class ApiParameter {
        private String in;
        private final String name;
        private final Class<?> javaType;
        private final String defaultValue;
    }

    private class SwaggerTemplate {

        private final Map<String, List<String>> templateByName;

        SwaggerTemplate() throws IOException {
            InputStream inputStream = new EzyAnywayInputStreamLoader()
                .load("swagger-template.txt");
            List<String> lines = EzyInputStreams.toLines(inputStream);
            templateByName = new HashMap<>();
            String currentTemplateName;
            List<String> currentTemplateLines = new ArrayList<>();
            for (String line : lines) {
                if (isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    currentTemplateName = line.substring(1).trim();
                    currentTemplateLines = new ArrayList<>();
                    templateByName.put(currentTemplateName, currentTemplateLines);
                    continue;
                }
                currentTemplateLines.add(line);
            }
        }

        private String createContent(
            String templateName,
            Map<String, Object> parameters,
            int doubleSpaces
        ) {
            List<String> lines = newArrayList(
                templateByName.get(templateName),
                it -> setLineParameters(it, parameters)
            );
            return joinString(lines, doubleSpaces);
        }

        private String setLineParameters(
            String line,
            Map<String, Object> parameters
        ) {
            String answer = line;
            for (Entry<String, Object> e : parameters.entrySet()) {
                answer = answer.replace(
                    "${" + e.getKey() + "}",
                    e.getValue().toString()
                );
            }
            return answer;
        }
    }

    private String joinString(
        List<String> lines,
        int doubleSpaces
    ) {
        List<String> notEmptyLines = lines
            .stream()
            .filter(EzyStrings::isNotBlank)
            .filter(it -> !it.trim().equals("'':"))
            .collect(Collectors.toList());
        if (doubleSpaces == 0) {
            return String.join("\n", notEmptyLines);
        }
        String spacesString = createSpaces(doubleSpaces);
        List<String> answer = new ArrayList<>();
        answer.add(notEmptyLines.get(0));
        for (int i = 1; i < notEmptyLines.size(); ++i) {
            answer.add(spacesString + notEmptyLines.get(i));
        }
        return String.join("\n", answer);
    }

    private String createSpaces(int doubleSpaces) {
        return EzyStrings.newString(
            ' ', doubleSpaces * 2
        );
    }

    private String mapJavaType(Class<?> javaType) {
        String type;
        if (javaType.isEnum()) {
            type = "string";
        } else if (EzyTypes.NON_ARRAY_TYPES.contains(javaType)) {
            if (javaType == boolean.class
                || javaType == Boolean.class
            ) {
                type = "boolean";
            } else if (javaType == int.class
                || javaType == Integer.class
                || javaType == byte.class
                || javaType == Byte.class
                || javaType == short.class
                || javaType == Short.class
            ) {
                type = "integer";
            } else if (javaType == long.class
                || javaType == Long.class
            ) {
                type = "integer";
            } else if (javaType == float.class
                || javaType == Float.class
            ) {
                type = "float";
            } else if (javaType == double.class
                || javaType == Double.class
            ) {
                type = "double";
            } else {
                type = "string";
            }
        } else if (javaType.isArray()) {
            type = "array";
        } else if (Collection.class.isAssignableFrom(javaType)) {
            type = "array";
        } else {
            type = "object";
        }
        return type;
    }
}
