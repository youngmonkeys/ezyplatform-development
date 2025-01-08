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

import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.Letter;
import org.youngmonkeys.ezyplatform.model.SimpleLetterModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescLetterPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterFilter;
import org.youngmonkeys.ezyplatform.pagination.LetterPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationLetterRepository;

public class PaginationLetterService extends CommonPaginationService<
    SimpleLetterModel,
    LetterFilter,
    LetterPaginationParameter,
    Long,
    Letter> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationLetterService(
        PaginationLetterRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        LetterPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected SimpleLetterModel convertEntity(Letter entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected LetterPaginationParameter defaultPaginationParameter() {
        return new IdDescLetterPaginationParameter();
    }
}
