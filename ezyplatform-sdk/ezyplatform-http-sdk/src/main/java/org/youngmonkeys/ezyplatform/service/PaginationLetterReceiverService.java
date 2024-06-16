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
import org.youngmonkeys.ezyplatform.entity.LetterReceiver;
import org.youngmonkeys.ezyplatform.model.LetterReceiverModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescLetterReceiverPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterReceiverFilter;
import org.youngmonkeys.ezyplatform.pagination.LetterReceiverPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterReceiverPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationLetterReceiverRepository;

public class PaginationLetterReceiverService extends CommonPaginationService<
    LetterReceiverModel,
    LetterReceiverFilter,
    LetterReceiverPaginationParameter,
    Long,
    LetterReceiver> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationLetterReceiverService(
        PaginationLetterReceiverRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        LetterReceiverPaginationParameterConverter letterReceiverPaginationParameterConverter
    ) {
        super(repository, letterReceiverPaginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected LetterReceiverModel convertEntity(LetterReceiver entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected LetterReceiverPaginationParameter defaultPaginationParameter() {
        return new IdDescLetterReceiverPaginationParameter();
    }
}
