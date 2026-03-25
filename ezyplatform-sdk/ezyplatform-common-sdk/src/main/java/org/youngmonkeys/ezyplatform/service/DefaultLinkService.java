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
import org.youngmonkeys.ezyplatform.entity.Link;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.LinkModel;
import org.youngmonkeys.ezyplatform.model.SaveLinkModel;
import org.youngmonkeys.ezyplatform.repo.LinkRepository;
import org.youngmonkeys.ezyplatform.result.TypeResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static org.youngmonkeys.ezyplatform.model.MediaNameModel.getMediaUrlOrNull;

@AllArgsConstructor
public class DefaultLinkService implements LinkService {

    private final MediaService mediaService;
    private final LinkRepository linkRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;
    private final DefaultEntityToModelConverter entityToModelConverter;

    @Override
    public void saveLink(SaveLinkModel model) {
        Link entity = linkRepository
            .findByField("linkUri", model.getLinkUri());
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(model);
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        linkRepository.save(entity);
    }

    @Override
    public void updateLink(
        long linkId,
        SaveLinkModel model
    ) {
        Link entity = getLinkByIdEntityOrThrow(linkId);
        modelToEntityConverter.mergeToEntity(model, entity);
        linkRepository.save(entity);
    }

    @Override
    public void removeLinkById(long linkId) {
        linkRepository.delete(linkId);
    }

    @Override
    public void removeLinkByUri(String uri) {
        linkRepository.deleteByLinkUri(uri);
    }

    @Override
    public boolean containsLinkByUri(String uri) {
        return linkRepository.containsByField(
            "linkUri",
            uri
        );
    }

    @Override
    public List<String> getAllLinkTypes() {
        return linkRepository
            .findAllLinkTypes()
            .stream()
            .map(TypeResult::getType)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getLinkTypesExclude(
        Collection<String> exclusiveLinkTypes
    ) {
        Collection<String> exclusive = exclusiveLinkTypes;
        if (exclusive.isEmpty()) {
            exclusive = Collections.singletonList(EMPTY_STRING);
        }
        return linkRepository
            .findLinkTypesExclude(exclusive)
            .stream()
            .map(TypeResult::getType)
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toList());
    }

    @Override
    public LinkModel getLinkById(long linkId) {
        return entityToModelConverter.toModel(
            linkRepository.findById(linkId),
            EMPTY_STRING
        );
    }

    @Override
    public LinkModel getLinkByIdIncludeImage(long linkId) {
        Link link = linkRepository.findById(linkId);
        return decorateLinkWithImage(link);
    }

    @Override
    public LinkModel getLinkByUri(String uri) {
        return entityToModelConverter.toModel(
            linkRepository.findByField("linkUri", uri),
            EMPTY_STRING
        );
    }

    @Override
    public LinkModel getLinkByUriIncludeImage(String uri) {
        Link link = linkRepository.findByField("linkUri", uri);
        return decorateLinkWithImage(link);
    }

    private LinkModel decorateLinkWithImage(Link link) {
        if (link == null) {
            return null;
        }
        long imageId = link.getImageId();
        return entityToModelConverter.toModel(
            link,
            imageId <= 0
                ? EMPTY_STRING
                : getMediaUrlOrNull(
                    mediaService.getMediaNameById(link.getImageId())
                )
        );
    }

    @Override
    public List<LinkModel> getLinksByIdGt(
        long idExclusive,
        int limit
    ) {
        return newArrayList(
            linkRepository.findByIdGt(
                idExclusive,
                Next.limit(limit)
            ),
            entityToModelConverter::toModel
        );
    }

    @Override
    public List<LinkModel> getLinksByTypeOrderByIdDesc(
        String linkType,
        int limit
    ) {
        return newArrayList(
            linkRepository.findByLinkTypeOrderByIdDesc(
                linkType,
                Next.limit(limit)
            ),
            entityToModelConverter::toModel
        );
    }

    protected Link getLinkByIdEntityOrThrow(long linkId) {
        Link link = linkRepository.findById(linkId);
        if (link == null) {
            throw new ResourceNotFoundException("link");
        }
        return link;
    }
}
