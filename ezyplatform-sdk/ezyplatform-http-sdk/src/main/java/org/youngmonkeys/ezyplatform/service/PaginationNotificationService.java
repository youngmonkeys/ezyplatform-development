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
import org.youngmonkeys.ezyplatform.entity.Notification;
import org.youngmonkeys.ezyplatform.entity.NotificationReceiver;
import org.youngmonkeys.ezyplatform.model.NotificationModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescNotificationPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationFilter;
import org.youngmonkeys.ezyplatform.pagination.NotificationPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.NotificationRepository;
import org.youngmonkeys.ezyplatform.repo.PaginationNotificationReceiverRepository;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperationSupplier;

public class PaginationNotificationService extends CommonPaginationService<
    NotificationModel,
    NotificationFilter,
    NotificationPaginationParameter,
    Long,
    NotificationReceiver> {

    private final NotificationRepository letterRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationNotificationService(
        PaginationNotificationReceiverRepository repository,
        NotificationRepository letterRepository,
        DefaultEntityToModelConverter entityToModelConverter,
        NotificationPaginationParameterConverter letterPaginationParameterConverter
    ) {
        super(repository, letterPaginationParameterConverter);
        this.letterRepository = letterRepository;
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected RxOperationSupplier<NotificationReceiver> convertEntityRxSupplier() {
        return letterReceiver -> Reactive.single(
            letterReceiver
        ).mapItem(it -> {
            Notification letter = letterRepository.findById(
                it.getNotificationId()
            );
            return entityToModelConverter.toModel(
                letter,
                it,
                false
            );
        });
    }

    @Override
    protected NotificationPaginationParameter defaultPaginationParameter() {
        return new IdDescNotificationPaginationParameter();
    }
}
