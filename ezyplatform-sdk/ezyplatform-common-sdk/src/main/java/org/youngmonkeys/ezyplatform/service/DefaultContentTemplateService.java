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

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.Next;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultResultToModelConverter;
import org.youngmonkeys.ezyplatform.data.TitleContent;
import org.youngmonkeys.ezyplatform.entity.ContentTemplate;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.ContentTemplateModel;
import org.youngmonkeys.ezyplatform.model.SaveContentTemplateModel;
import org.youngmonkeys.ezyplatform.repo.ContentTemplateRepository;
import org.youngmonkeys.ezyplatform.result.ContentTypeResult;
import org.youngmonkeys.ezyplatform.result.TemplateTypeResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultContentTemplateService
    implements ContentTemplateService {

    private final ContentTemplateRepository contentTemplateRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;
    private final DefaultResultToModelConverter resultToModelConverter;

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
    public void deleteTemplate(long templateId) {
        contentTemplateRepository.delete(templateId);
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
    public List<ContentTemplateModel> getTemplatesByType(
        String templateType,
        int limit
    ) {
        return newArrayList(
            contentTemplateRepository.findTemplatesByType(
                templateType,
                Next.limit(limit)
            ),
            resultToModelConverter::toModel
        );
    }

    @Override
    public List<ContentTemplateModel> getTemplatesByTypes(
        Collection<String> templateTypes,
        int limit
    ) {
        if (templateTypes.isEmpty()) {
            return Collections.emptyList();
        }
        return newArrayList(
            contentTemplateRepository.findTemplatesByTypeIn(
                templateTypes,
                Next.limit(limit)
            ),
            resultToModelConverter::toModel
        );
    }

    @Override
    public List<String> getAllTemplateTypes() {
        return newArrayList(
            contentTemplateRepository.findAllTemplateTypes(),
            TemplateTypeResult::getTemplateType
        );
    }

    @Override
    public List<String> getAllTemplateContentTypes() {
        return contentTemplateRepository
            .findAllContentTypes()
            .stream()
            .map(ContentTypeResult::getContentType)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public ContentTemplateModel getTemplateByOwnerTypeAndOwnerIdAndTemplateTypeAndTemplateName(
        String ownerType,
        long ownerId,
        String templateType,
        String templateName
    ) {
        return entityToModelConverter.toModel(
            contentTemplateRepository
                .findByOwnerTypeAndOwnerIdAndTemplateTypeAndTemplateName(
                    ownerType,
                    ownerId,
                    templateType,
                    templateName
                )
        );
    }

    @Override
    public ContentTemplateModel getTemplateByCreatorTypeAndCreatorIdAndTemplateTypeAndTemplateName(
        String creatorType,
        long creatorId,
        String templateType,
        String templateName
    ) {
        return entityToModelConverter.toModel(
            contentTemplateRepository
                .findByCreatorTypeAndCreatorIdAndTemplateTypeAndTemplateName(
                    creatorType,
                    creatorId,
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

    @Override
    public long countTemplatesByType(String templateType) {
        return contentTemplateRepository.countByTemplateType(
            templateType
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
