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

package org.youngmonkeys.ezyplatform.fetcher;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TemplateParametersSchemaFetcherManager {

    private final EzyLazyInitializer<Map<String, Map<String, TemplateParametersSchemaFetcher>>>
        templateParametersSchemaFetcherByTemplateNameByTemplateType;
    private final Map<String, Map<String, TemplateParametersSchemaFetcher>>
        additionalTemplateParametersSchemaFetcherByTemplateNameByTemplateType;

    @SuppressWarnings("unchecked")
    public TemplateParametersSchemaFetcherManager(
        EzySingletonFactory singletonFactory
    ) {
        this.templateParametersSchemaFetcherByTemplateNameByTemplateType =
            new EzyLazyInitializer<>(() ->
                ((List<TemplateParametersSchemaFetcher>) singletonFactory
                    .getSingletonsOf(TemplateParametersSchemaFetcher.class)
                )
                    .stream()
                    .collect(
                        Collectors.groupingBy(
                            TemplateParametersSchemaFetcher::getTemplateName,
                            Collectors.toMap(
                                TemplateParametersSchemaFetcher::getTemplateType,
                                it -> it,
                                (o, n) -> n
                            )
                        )
                    )
        );
        this.additionalTemplateParametersSchemaFetcherByTemplateNameByTemplateType =
            new ConcurrentHashMap<>();
    }

    public void addTemplateParametersSchemaFetcher(
        TemplateParametersSchemaFetcher fetcher
    ) {
        additionalTemplateParametersSchemaFetcherByTemplateNameByTemplateType
            .computeIfAbsent(
                fetcher.getTemplateType(),
                k -> new ConcurrentHashMap<>()
            )
            .put(fetcher.getTemplateName(), fetcher);
    }

    @SuppressWarnings("LineLength")
    public TemplateParametersSchemaFetcher getTemplateParametersSchemaFetcherByTemplateTypeAndTemplateName(
        String templateType,
        String templateName
    ) {
        TemplateParametersSchemaFetcher fetcher =
            templateParametersSchemaFetcherByTemplateNameByTemplateType
                .get()
                .getOrDefault(templateType, Collections.emptyMap())
                .get(templateName);
        if (fetcher == null) {
            fetcher = additionalTemplateParametersSchemaFetcherByTemplateNameByTemplateType
                .getOrDefault(templateType, Collections.emptyMap())
                .get(templateName);
        }
        return fetcher;
    }

    public List<TemplateParametersSchemaFetcher> getTemplateParametersSchemaFetchers() {
        List<TemplateParametersSchemaFetcher> answer =
            templateParametersSchemaFetcherByTemplateNameByTemplateType
                .get()
                .entrySet()
                .stream()
                .flatMap(it -> it.getValue().values().stream())
                .collect(Collectors.toList());
        answer.addAll(
            additionalTemplateParametersSchemaFetcherByTemplateNameByTemplateType
                .entrySet()
                .stream()
                .flatMap(it -> it.getValue().values().stream())
                .collect(Collectors.toList())
        );
        return answer;
    }
}
