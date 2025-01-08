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
import org.youngmonkeys.ezyplatform.entity.NotificationReceiver;
import org.youngmonkeys.ezyplatform.model.NotificationReceiverModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescNotificationReceiverPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationReceiverFilter;
import org.youngmonkeys.ezyplatform.pagination.NotificationReceiverPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationReceiverPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationNotificationReceiverRepository;

public class PaginationNotificationReceiverService extends CommonPaginationService<
    NotificationReceiverModel,
    NotificationReceiverFilter,
    NotificationReceiverPaginationParameter,
    Long,
    NotificationReceiver> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationNotificationReceiverService(
        PaginationNotificationReceiverRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        NotificationReceiverPaginationParameterConverter
            paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected NotificationReceiverModel convertEntity(NotificationReceiver entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected NotificationReceiverPaginationParameter defaultPaginationParameter() {
        return new IdDescNotificationReceiverPaginationParameter();
    }
}
