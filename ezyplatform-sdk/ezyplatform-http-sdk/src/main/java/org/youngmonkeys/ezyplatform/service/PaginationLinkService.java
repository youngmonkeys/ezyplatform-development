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

import com.tvd12.ezyhttp.server.core.annotation.Service;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.Link;
import org.youngmonkeys.ezyplatform.model.LinkModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescLinkPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LinkFilter;
import org.youngmonkeys.ezyplatform.pagination.LinkPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LinkPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationLinkRepository;

@Service
public class PaginationLinkService extends DefaultPaginationService<
    LinkModel,
    LinkFilter,
    LinkPaginationParameter,
    Long,
    Link> {

    private final DefaultEntityToModelConverter entityToModelConverter;
    private final LinkPaginationParameterConverter linkPaginationParameterConverter;

    public PaginationLinkService(
        PaginationLinkRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        LinkPaginationParameterConverter parameterConverter
    ) {
        super(repository);
        this.entityToModelConverter = entityToModelConverter;
        this.linkPaginationParameterConverter = parameterConverter;
    }

    @Override
    protected LinkModel convertEntity(Link entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected String serializeToPageToken(
        LinkPaginationParameter paginationParameter,
        LinkModel model
    ) {
        return linkPaginationParameterConverter.serialize(
            paginationParameter.sortOrder(),
            model
        );
    }

    @Override
    protected LinkPaginationParameter deserializePageToken(String value) {
        return linkPaginationParameterConverter.deserialize(
            value
        );
    }

    @Override
    protected LinkPaginationParameter defaultPaginationParameter() {
        return new IdDescLinkPaginationParameter();
    }
}
