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

package org.youngmonkeys.ezyplatform.manager;

import com.tvd12.ezyfox.io.EzySets;
import org.youngmonkeys.ezyplatform.constant.CommonContentType;
import org.youngmonkeys.ezyplatform.service.ContentTemplateService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

public class TemplateContentTypeManager {

    private final Set<String> additionalTemplateContentTypes;
    private final ContentTemplateService contentTemplateService;

    public TemplateContentTypeManager(
        ContentTemplateService contentTemplateService
    ) {
        this.contentTemplateService = contentTemplateService;
        this.additionalTemplateContentTypes =
            ConcurrentHashMap.newKeySet();
    }

    public void addTemplateContentType(String contentType) {
        additionalTemplateContentTypes.add(contentType);
    }

    public void addTemplateContentTypes(String... contentTypes) {
        addTemplateContentTypes(Arrays.asList(contentTypes));
    }

    public void addTemplateContentTypes(
        Collection<String> contentTypes
    ) {
        contentTypes.forEach(this::addTemplateContentType);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAllTemplateContentTypes() {
        return EzySets.combine(
            newArrayList(
                CommonContentType.values(),
                CommonContentType::toString
            ),
            contentTemplateService.getAllTemplateContentTypes(),
            additionalTemplateContentTypes
        );
    }

    public List<String> getAllSortedTemplateContentTypes() {
        return getAllTemplateContentTypes()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
