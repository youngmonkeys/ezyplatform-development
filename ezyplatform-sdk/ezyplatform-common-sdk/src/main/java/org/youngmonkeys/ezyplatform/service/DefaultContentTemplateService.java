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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.data.TitleContent;
import org.youngmonkeys.ezyplatform.entity.ContentTemplate;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.ContentTemplateModel;
import org.youngmonkeys.ezyplatform.model.SaveContentTemplateModel;
import org.youngmonkeys.ezyplatform.repo.ContentTemplateRepository;

import java.util.Map;

@AllArgsConstructor
public class DefaultContentTemplateService
    implements ContentTemplateService {

    private final ContentTemplateRepository contentTemplateRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public long addTemplate(
        String creatorType,
        long creatorId,
        SaveContentTemplateModel model
    ) {
        ContentTemplate entity = modelToEntityConverter
            .toEntity(
                creatorType,
                creatorId,
                model
            );
        contentTemplateRepository.save(entity);
        return entity.getId();
    }

    @Override
    public void updateTemplate(
        long templateId,
        SaveContentTemplateModel model
    ) {
        ContentTemplate entity = getContentTemplateEntityByIdOrThrow(
            templateId
        );
        modelToEntityConverter.mergeToEntity(model, entity);
        contentTemplateRepository.save(entity);
    }

    @Override
    public long saveTemplate(
        String creatorType,
        long creatorId,
        SaveContentTemplateModel model
    ) {
        ContentTemplate entity = contentTemplateRepository
            .findByOwnerTypeAndOwnerIdAndTemplateTypeAndTemplateName(
                model.getOwnerType(),
                model.getOwnerId(),
                model.getTemplateType(),
                model.getTemplateName()
            );
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(
                creatorType,
                creatorId,
                model
            );
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        contentTemplateRepository.save(entity);
        return entity.getId();
    }

    @Override
    public ContentTemplateModel getTemplateById(
        long templateId
    ) {
        return entityToModelConverter.toModel(
            contentTemplateRepository.findById(templateId)
        );
    }

    @Override
    public ContentTemplateModel getTemplateByTypeAndName(
        String templateType,
        String templateName
    ) {
        return entityToModelConverter.toModel(
            contentTemplateRepository.findByTemplateTypeAndTemplateName(
                templateType,
                templateName
            )
        );
    }

    @Override
    public boolean containsTemplateByTypeAndName(
        String templateType,
        String templateName
    ) {
        return contentTemplateRepository.countByTemplateTypeAndTemplateName(
            templateType,
            templateName
        ) > 0;
    }

    @Override
    public TitleContent makeTitleContent(
        String templateType,
        String templateName,
        Map<String, Object> parameters
    ) {
        ContentTemplateModel template = getTemplateByTypeAndName(
            templateType,
            templateName
        );
        return TitleContent.fromTemplates(
            template.getTitleTemplate(),
            template.getContentTemplate(),
            parameters
        );
    }

    protected ContentTemplate getContentTemplateEntityByIdOrThrow(
        long contentTemplateId
    ) {
        ContentTemplate entity = contentTemplateRepository
            .findById(contentTemplateId);
        if (entity == null) {
            throw new ResourceNotFoundException("contentTemplate");
        }
        return entity;
    }
}
