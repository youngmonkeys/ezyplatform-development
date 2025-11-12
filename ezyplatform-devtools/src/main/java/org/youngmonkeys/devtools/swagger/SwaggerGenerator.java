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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.file.EzySimpleFileWriter;
import com.tvd12.ezyfox.io.EzyCollections;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.*;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.filter;
import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

@SuppressWarnings("MethodCount")
@AllArgsConstructor
public class SwaggerGenerator {

    private final String packageToScan;

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;

    private static final String PREFIX_SET = "set";
    private static final String PREFIX_GET = "get";

    private static final Class<?> NO_GENERICS_TYPE = null;
    private static final List<ApiDataField> NO_FIELDS = null;

    private static final Set<String> NO_CONTENT_JAVA_TYPES =
        Sets.newHashSet(
            "redirect",
            "response_entity",
            "void"
        );
    private static final Map<String, String> CONTENT_TYPE_BY_JAVA_TYPE_NAME =
        EzyMapBuilder.mapBuilder()
            .put("view", "text/html")
            .toMap();
    private static final Map<String, String> SWAGGER_TYPE_BY_JAVA_TYPE_NAME =
        EzyMapBuilder.mapBuilder()
            .put("double", "number")
            .put("float", "number")
            .put("long", "number")
            .put("view", "string")
            .toMap();
    private static final Map<String, String> SWAGGER_FORMAT_BY_JAVA_TYPE_NAME =
        EzyMapBuilder.mapBuilder()
            .put("double", "number")
            .put("float", "float")
            .put("integer", "int32")
            .put("long", "int64")
            .toMap();

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

    private Map<Class<?>, String> getTypeByJavaClass() {
        return Collections.emptyMap();
    }

    public String generate() {
        EzyReflection reflection = new EzyReflectionProxy(
            packageToScan
        );
        Set<Class<?>> controllerClasses = reflection.getAnnotatedClasses(
            Controller.class
        );
        return generate(controllerClasses);
    }

    public String generate(
        Collection<Class<?>> controllerClasses
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("openapi: 3.0.2");
        lines.add("info:");
        lines.add(appendDoubleSpacesToLine("title: EzyPlatform APIs", ONE));
        lines.add(appendDoubleSpacesToLine("version: 1.0.0", ONE));
        lines.add("servers:");
        lines.addAll(appendDoubleSpacesToLines(createServers(), ONE));
        lines.add("paths:");
        lines.addAll(
            appendDoubleSpacesToLines(
                createApis(controllerClasses),
                ONE
            )
        );
        return joinLine(lines);
    }

    private List<String> createServers() {
        return getServers()
            .stream()
            .flatMap(it -> createServer(it).stream())
            .collect(Collectors.toList());
    }

    private List<String> createServer(
        Map<String, Object> server
    ) {
        return Arrays.asList(
            "- url: " + server.get("url"),
            "  description: " + server.get("description")
        );
    }

    protected List<Map<String, Object>> getServers() {
        return Collections.singletonList(
            EzyMapBuilder
                .mapBuilder()
                .put("url", "http://localhost:8080")
                .put("description", "localhost")
                .toMap()
        );
    }

    private List<String> createApis(
        Collection<Class<?>> controllerClasses
    ) {
        List<ApiClass> apiClasses = newArrayList(
            controllerClasses,
            this::extractControllerClass
        );
        Set<ApiMethod> generatedMethods = new HashSet<>();
        List<String> apis = new ArrayList<>();
        for (ApiClass apiClass : apiClasses) {
            for (String uri : apiClass.methodsByUri.keySet()) {
                List<ApiMethod> apiMethods = filter(
                    apiClass.methodsByUri.get(uri),
                    it -> !generatedMethods.contains(it)
                );
                for (int i = 0; i < apiMethods.size(); ++i) {
                    ApiMethod apiMethod = apiMethods.get(i);
                    generatedMethods.add(apiMethod);
                    if (i == 0) {
                        apis.add("'" + uri + "':");
                    }
                    apis.addAll(createApi(apiMethod));
                }
            }
        }
        return apis;
    }

    private List<String> createApi(ApiMethod apiMethod) {
        List<String> lines = new ArrayList<>();
        lines.add(
            appendDoubleSpacesToLine(
                apiMethod.type + ":",
                ONE
            )
        );
        lines.add(appendDoubleSpacesToLine("summary: >", TWO));
        lines.add(
            appendDoubleSpacesToLine(
                toFriendlyText(apiMethod.name),
                THREE
            )
        );
        lines.add(appendDoubleSpacesToLine("description: >", TWO));
        lines.addAll(
            appendDoubleSpacesToLines(
                createApiDescription(apiMethod),
                THREE
            )
        );
        lines.addAll(
            appendDoubleSpacesToLines(
                createApiRequestBody(
                    apiMethod
                ),
                TWO
            )
        );
        lines.addAll(
            appendDoubleSpacesToLines(
                createApiParameters(
                    apiMethod.parameters
                ),
                TWO
            )
        );
        lines.add(appendDoubleSpacesToLine("responses:", TWO));
        lines.addAll(
            appendDoubleSpacesToLines(
                createApiResponseBody(
                    apiMethod
                ),
                THREE
            )
        );
        return lines;
    }

    private List<String> createApiDescription(ApiMethod apiMethod) {
        List<String> lines = new ArrayList<>();
        lines.add("- feature: " + toFriendlyText(apiMethod.feature));
        lines.add("- api: " + (apiMethod.api ? "yes" : "no"));
        lines.add("- authenticated: " + (apiMethod.authenticated ? "yes" : "no"));
        return lines;
    }

    private List<String> createApiRequestBody(
        ApiMethod apiMethod
    ) {
        ApiDataType requestBody = apiMethod.requestBody;
        if (requestBody == null) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("requestBody: ");
        lines.add(appendDoubleSpacesToLine("required: true", ONE));
        lines.add(appendDoubleSpacesToLine("content:", ONE));
        lines.addAll(
            appendDoubleSpacesToLines(
                convertApiDataToContent(
                    requestBody,
                    PREFIX_SET
                ),
                TWO
            )
        );
        return lines;
    }

    private List<String> createApiParameters(
        List<ApiParameter> apiParameters
    ) {
        if (apiParameters.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("parameters:");
        List<String> parameters = apiParameters
            .stream()
            .flatMap(it -> createApiParameter(it).stream())
            .collect(Collectors.toList());
        lines.addAll(appendDoubleSpacesToLines(parameters, ONE));
        return lines;
    }

    private List<String> createApiParameter(
        ApiParameter apiParameter
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("- name: " + apiParameter.name);
        lines.add("  in: " + apiParameter.in);
        String description = apiParameter.defaultValue != null
            ? "default value is " + apiParameter.defaultValue
            : "parameter";
        lines.add("  description: " + description);
        lines.add("  required: " + "path".equals(apiParameter.in));
        lines.addAll(
            appendDoubleSpacesToLines(
                convertApiDataTypeToSchema(
                    apiParameter.apiDataType,
                    PREFIX_SET
                ),
                ONE
            )
        );
        return lines;
    }

    private List<String> createApiResponseBody(
        ApiMethod apiMethod
    ) {
        String code = "200";
        String description = "success";
        ApiDataType response = apiMethod.response;
        if (response.type == Redirect.class) {
            code = "302";
            description = "redirect";
        } else if (response.type == ResponseEntity.class) {
            code = "204";
            description = "no content";
        } else if (response.type == View.class) {
            description = "return a view";
        }
        List<String> lines = new ArrayList<>();
        lines.add("'" + code + "':");
        lines.add(appendDoubleSpacesToLine("description: >", ONE));
        lines.add(appendDoubleSpacesToLine(description, TWO));
        String javaType = response.javaType;
        if (!NO_CONTENT_JAVA_TYPES.contains(javaType)) {
            lines.add(appendDoubleSpacesToLine("content:", ONE));
            lines.addAll(
                appendDoubleSpacesToLines(
                    convertApiDataToContent(response, PREFIX_GET),
                    TWO
                )
            );
        }
        return lines;
    }

    private List<String> convertApiDataToContent(
        ApiDataType apiDataType,
        String methodPrefix
    ) {
        List<String> lines = new ArrayList<>();
        String contentType = CONTENT_TYPE_BY_JAVA_TYPE_NAME
            .getOrDefault(
                apiDataType.javaType,
                "application/json"
            );
        lines.add(contentType + ":");
        lines.addAll(
            appendDoubleSpacesToLines(
                convertApiDataTypeToSchema(
                    apiDataType,
                    methodPrefix
                ),
                ONE
            )
        );
        return lines;
    }

    private List<String> convertApiDataTypeToSchema(
        ApiDataType apiDataType,
        String methodPrefix
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("schema:");
        String javaType = apiDataType.javaType;
        lines.addAll(
            appendDoubleSpacesToLines(
                createTypeFormat(javaType),
                ONE
            )
        );
        lines.addAll(
            appendDoubleSpacesToLines(
                createProperties(
                    apiDataType,
                    methodPrefix,
                    new HashMap<>()
                ),
                ONE
            )
        );
        return lines;
    }

    private List<String> createTypeFormat(
        String javaType
    ) {
        List<String> lines = new ArrayList<>();
        String swaggerType = SWAGGER_TYPE_BY_JAVA_TYPE_NAME
            .getOrDefault(javaType, javaType);
        lines.add("type: " + swaggerType);
        String swaggerFormat = SWAGGER_FORMAT_BY_JAVA_TYPE_NAME
            .get(javaType);
        if (swaggerFormat != null) {
            lines.add("format: " + swaggerFormat);
        }
        return lines;
    }

    private List<String> createProperties(
        ApiDataType apiDataType,
        String methodPrefix,
        Map<Class<?>, Set<Class<?>>> dependenciesByType
    ) {
        String javaType = apiDataType.javaType;
        if ("object".equals(javaType)) {
            return createObjectProperties(
                apiDataType.fields,
                methodPrefix,
                dependenciesByType
            );
        } else if ("array".equals(javaType)) {
            return createItems(
                apiDataType.genericsType,
                methodPrefix,
                dependenciesByType
            );
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> createObjectProperties(
        List<ApiDataField> fields,
        String methodPrefix,
        Map<Class<?>, Set<Class<?>>> dependenciesByType
    ) {
        if (EzyCollections.isEmpty(fields)) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("properties:");
        for (ApiDataField field : fields) {
            lines.addAll(
                appendDoubleSpacesToLines(
                    createFieldProperties(
                        field,
                        methodPrefix,
                        dependenciesByType
                    ),
                    ONE
                )
            );
        }
        return lines;
    }

    @SuppressWarnings("MethodLength")
    private List<String> createFieldProperties(
        ApiDataField field,
        String methodPrefix,
        Map<Class<?>, Set<Class<?>>> dependenciesByType
    ) {
        List<String> lines = new ArrayList<>();
        lines.add(field.name + ":");
        String fieldJavaType = mapJavaType(field.javaType);
        lines.addAll(
            appendDoubleSpacesToLines(
                createTypeFormat(fieldJavaType),
                ONE
            )
        );
        if ("array".equals(fieldJavaType)) {
            Class<?> fieldGenericsType = getGenericType(
                field.javaGenericsType
            );
            if (fieldGenericsType == null) {
                fieldGenericsType = field.javaType.getComponentType();
            }
            Set<Class<?>> dependencies = dependenciesByType
                .computeIfAbsent(fieldGenericsType, k -> new HashSet<>());
            if (dependencies.contains(fieldGenericsType)) {
                lines.add(appendDoubleSpacesToLine("items:", ONE));
                lines.add(appendDoubleSpacesToLine("type: object", TWO));
                return lines;
            }
            dependencies.add(fieldGenericsType);
            lines.addAll(
                appendDoubleSpacesToLines(
                    createItems(
                        fieldGenericsType,
                        methodPrefix,
                        dependenciesByType
                    ),
                    ONE
                )
            );
        } else if ("object".equals(fieldJavaType)) {
            Set<Class<?>> dependencies = dependenciesByType
                .computeIfAbsent(field.javaType, k -> new HashSet<>());
            if (dependencies.contains(field.javaType)) {
                return lines;
            }
            dependencies.add(field.javaType);
            lines.addAll(
                appendDoubleSpacesToLines(
                    createProperties(
                        new ApiDataType(
                            fieldJavaType,
                            field.javaType,
                            NO_GENERICS_TYPE,
                            extractApiDataFields(
                                field.javaType,
                                methodPrefix
                            )
                        ),
                        methodPrefix,
                        dependenciesByType
                    ),
                    ONE
                )
            );
        }
        return lines;
    }

    private List<String> createItems(
        Class<?> genericsType,
        String methodPrefix,
        Map<Class<?>, Set<Class<?>>> dependenciesByType
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("items:");
        String genericsTypeName;
        if (genericsType == null) {
            genericsTypeName = "object";
        } else if (EzyTypes.NON_ARRAY_TYPES.contains(genericsType)) {
            genericsTypeName = genericsType.getSimpleName().toLowerCase();
        } else if (Collection.class.isAssignableFrom(genericsType)) {
            genericsTypeName = "array";
        } else {
            genericsTypeName = "object";
        }
        lines.addAll(
            appendDoubleSpacesToLines(
                createTypeFormat(genericsTypeName),
                ONE
            )
        );
        if ("object".equals(genericsTypeName)) {
            lines.addAll(
                appendDoubleSpacesToLines(
                    createProperties(
                        new ApiDataType(
                            genericsTypeName,
                            genericsType,
                            NO_GENERICS_TYPE,
                            extractApiDataFields(
                                genericsType,
                                methodPrefix
                            )
                        ),
                        methodPrefix,
                        dependenciesByType
                    ),
                    ONE
                )
            );
        } else if ("array".equals(genericsTypeName)) {
            lines.addAll(
                appendDoubleSpacesToLines(
                    createItems(
                        NO_GENERICS_TYPE,
                        methodPrefix,
                        dependenciesByType
                    ),
                    ONE
                )
            );
        }
        return lines;
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
        String uri = null;
        if (controllerAnnotation != null) {
            uri = controllerAnnotation.uri();
            if (isBlank(uri)) {
                uri = controllerAnnotation.value();
            }
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
                it.isAnnotationPresent(DoDelete.class)
                    || it.isAnnotationPresent(DoGet.class)
                    || it.isAnnotationPresent(DoPost.class)
                    || it.isAnnotationPresent(DoPut.class)
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

    @SuppressWarnings("MethodLength")
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
            PathVariable pathVariableAnnotation = parameter.getAnnotation(
                PathVariable.class
            );
            if (pathVariableAnnotation != null) {
                in = "path";
                name = pathVariableAnnotation.value();
                if (isBlank(name)) {
                    name = pathVariableIndex < pathVariables.size()
                        ? pathVariables.get(pathVariableIndex)
                        : "unknown";
                }
                ++pathVariableIndex;
            }
            RequestParam requestParamAnnotation = parameter.getAnnotation(
                RequestParam.class
            );
            if (requestParamAnnotation != null) {
                in = "query";
                name = requestParamAnnotation.value();
                if (isBlank(name)) {
                    name = parameter.getName();
                }
                defaultValue = requestParamAnnotation.defaultValue();
            }
            if (in != null) {
                answer.add(
                    new ApiParameter(
                        in,
                        name,
                        defaultValue,
                        createApiDataType(
                            parameter.getType(),
                            getGenericType(parameter.getParameterizedType()),
                            PREFIX_SET
                        )
                    )
                );
            }
        }
        return answer;
    }

    private ApiDataType extractRequestBody(Method method) {
        Parameter requestBodyParam = null;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                requestBodyParam = parameter;
                break;
            }
        }
        if (requestBodyParam == null) {
            return null;
        }
        return createApiDataType(
            requestBodyParam.getType(),
            requestBodyParam.getParameterizedType(),
            PREFIX_SET
        );
    }

    private ApiDataType extractResponse(Method method) {
        return createApiDataType(
            method.getReturnType(),
            method.getGenericReturnType(),
            PREFIX_GET
        );
    }

    private ApiDataType createApiDataType(
        Class<?> type,
        Type genericsParameterizedType,
        String methodPrefix
    ) {
        String javaType = mapJavaType(type);
        List<ApiDataField> fields = NO_FIELDS;
        if ("object".equals(javaType)) {
            fields = extractApiDataFields(
                type,
                methodPrefix
            );
        }
        return new ApiDataType(
            javaType,
            type,
            getGenericType(genericsParameterizedType),
            fields
        );
    }

    @SuppressWarnings("MethodLength")
    private List<ApiDataField> extractApiDataFields(
        Class<?> clazz,
        String methodPrefix
    ) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        Map<String, Field> fieldByName = newHashMap(
            EzyFields.getFields(clazz),
            Field::getName
        );
        boolean isSetter = PREFIX_SET.equals(methodPrefix);
        return EzyMethods.getPublicMethods(clazz)
            .stream()
            .filter(
                it -> (it.getName().startsWith(methodPrefix)
                    || it.getName().startsWith("is"))
                    && (isSetter
                    ? it.getParameterCount() == 1
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
            .filter(it -> {
                Field field = fieldByName.get(it.name);
                if (field == null) {
                    return true;
                }
                return !field.isAnnotationPresent(JsonIgnore.class);
            })
            .map(it -> {
                Field field = fieldByName.get(it.name);
                if (field != null) {
                    JsonProperty jsonProperty = field.getDeclaredAnnotation(
                        JsonProperty.class
                    );
                    if (jsonProperty != null) {
                        String name = jsonProperty.value();
                        if (isNotBlank(name)) {
                            return new ApiDataField(
                                name,
                                it.javaType,
                                it.javaGenericsType
                            );
                        }
                    }
                }
                return it;
            })
            .distinct()
            .sorted(Comparator.comparing(a -> a.name))
            .collect(Collectors.toList());
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
    @EqualsAndHashCode(of = {"type", "uri"})
    public static class ApiMethod {
        private final String name;
        private final String type;
        private final String uri;
        private final String feature;
        private final boolean api;
        private final boolean authenticated;
        private final List<ApiParameter> parameters;
        private final ApiDataType requestBody;
        private final ApiDataType response;
    }

    @AllArgsConstructor
    private static class ApiDataType {
        private final String javaType;
        private final Class<?> type;
        private final Class<?> genericsType;
        private final List<ApiDataField> fields;
    }

    @AllArgsConstructor
    @EqualsAndHashCode(of = "name")
    private static class ApiDataField {
        private final String name;
        private final Class<?> javaType;
        private final Type javaGenericsType;
    }

    @AllArgsConstructor
    private static class ApiParameter {
        private String in;
        private final String name;
        private final String defaultValue;
        private final ApiDataType apiDataType;
    }

    private String appendDoubleSpacesToLine(
        String line,
        int doubleSpaces
    ) {
        String spacesString = createSpaces(doubleSpaces);
        return spacesString + line;
    }

    private List<String> appendDoubleSpacesToLines(
        List<String> lines,
        int doubleSpaces
    ) {
        String spacesString = createSpaces(doubleSpaces);
        return lines
            .stream()
            .filter(EzyStrings::isNotBlank)
            .filter(it -> !it.trim().equals("'':"))
            .map(it -> spacesString + it)
            .collect(Collectors.toList());
    }

    private String createSpaces(int doubleSpaces) {
        return EzyStrings.newString(
            ' ', doubleSpaces * 2
        );
    }

    private String joinLine(Collection<String> lines) {
        return String.join("\n", lines);
    }

    @SuppressWarnings("MethodLength")
    private String mapJavaType(Class<?> javaType) {
        if (javaType == null) {
            return "object";
        }
        Map<Class<?>, String> typeByJavaClass = getTypeByJavaClass();
        String type = typeByJavaClass.get(javaType);
        if (type != null) {
            return type;
        }
        if (javaType == void.class) {
            type = "void";
        } else if (javaType == Redirect.class) {
            type = "redirect";
        } else if (javaType == ResponseEntity.class) {
            type = "response_entity";
        } else if (javaType == View.class) {
            type = "view";
        } else if (javaType == LocalDateTime.class) {
            type = "string";
        } else if (javaType == LocalDate.class) {
            type = "string";
        } else if (javaType.isEnum()) {
            type = "string";
        } else if (javaType == BigInteger.class) {
            type = "integer";
        } else if (javaType == BigDecimal.class) {
            type = "double";
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
                type = "long";
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

    private String toFriendlyText(String s) {
        if (isBlank(s)) {
            return s;
        }
        String withSpaces = s
            .replaceAll("[^\\p{L}\\p{Nd}]+", " ")
            .replaceAll("([\\p{Ll}\\p{Nd}])(\\p{Lu})", "$1 $2")
            .replaceAll("(\\p{Lu}+)(\\p{Lu}\\p{Ll})", "$1 $2")
            .replaceAll("(\\p{L})(\\p{Nd})", "$1 $2")
            .replaceAll("(\\p{Nd})(\\p{L})", "$1 $2")
            .trim()
            .replaceAll("\\s+", " ");
        String lower = Arrays.stream(withSpaces.split("\\s+"))
            .map(String::toLowerCase)
            .collect(Collectors.joining(" "));
        return lower.isEmpty()
            ? lower
            : Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
