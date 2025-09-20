/*
 * Copyright 2022 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.data.TitleContent;
import org.youngmonkeys.ezyplatform.model.ContentTemplateModel;
import org.youngmonkeys.ezyplatform.model.SaveContentTemplateModel;

import java.util.List;
import java.util.Map;

public interface ContentTemplateService {

    long addTemplate(
        String creatorType,
        long creatorId,
        SaveContentTemplateModel model
    );

    void updateTemplate(
        long templateId,
        SaveContentTemplateModel model
    );

    long saveTemplate(
        String creatorType,
        long creatorId,
        SaveContentTemplateModel model
    );

    void deleteTemplate(long templateId);

    ContentTemplateModel getTemplateById(
        long templateId
    );

    ContentTemplateModel getTemplateByTypeAndName(
        String templateType,
        String templateName
    );

    List<ContentTemplateModel> getTemplatesByType(
        String templateType,
        int limit
    );

    List<String> getAllTemplateTypes();

    List<String> getAllTemplateContentTypes();

    boolean containsTemplateByTypeAndName(
        String templateType,
        String templateName
    );

    TitleContent makeTitleContent(
        String templateType,
        String templateName,
        Map<String, Object> parameters
    );

    long countTemplatesByType(String templateType);
}
