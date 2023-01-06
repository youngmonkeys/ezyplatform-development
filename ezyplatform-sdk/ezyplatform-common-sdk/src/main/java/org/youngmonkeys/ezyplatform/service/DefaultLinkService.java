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
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.Link;
import org.youngmonkeys.ezyplatform.model.SaveLinkModel;
import org.youngmonkeys.ezyplatform.repo.LinkRepository;

@AllArgsConstructor
public class DefaultLinkService implements LinkService {

    private final LinkRepository linkRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

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
    public void removeLink(long linkId) {
        linkRepository.delete(linkId);
    }

    @Override
    public void removeLinkByUri(String uri) {
        linkRepository.deleteByLinkUri(uri);
    }
}
