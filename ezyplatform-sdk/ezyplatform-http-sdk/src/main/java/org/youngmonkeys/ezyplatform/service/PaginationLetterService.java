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
import org.youngmonkeys.ezyplatform.entity.LetterReceiver;
import org.youngmonkeys.ezyplatform.model.LetterModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescLetterPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterFilter;
import org.youngmonkeys.ezyplatform.pagination.LetterPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.LetterPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.LetterRepository;
import org.youngmonkeys.ezyplatform.repo.PaginationLetterReceiverRepository;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperationSupplier;

public class PaginationLetterService extends CommonPaginationService<
    LetterModel,
    LetterFilter,
    LetterPaginationParameter,
    Long,
    LetterReceiver> {

    private final LetterRepository letterRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationLetterService(
        PaginationLetterReceiverRepository repository,
        LetterRepository letterRepository,
        DefaultEntityToModelConverter entityToModelConverter,
        LetterPaginationParameterConverter letterPaginationParameterConverter
    ) {
        super(repository, letterPaginationParameterConverter);
        this.letterRepository = letterRepository;
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected RxOperationSupplier<LetterReceiver> convertEntityRxSupplier() {
        return letterReceiver -> Reactive.single(
            letterReceiver
        ).mapItem(it -> {
            Letter letter = letterRepository.findById(
                it.getLetterId()
            );
            return entityToModelConverter.toModel(
                letter,
                it,
                false
            );
        });
    }

    @Override
    protected LetterPaginationParameter defaultPaginationParameter() {
        return new IdDescLetterPaginationParameter();
    }
}
