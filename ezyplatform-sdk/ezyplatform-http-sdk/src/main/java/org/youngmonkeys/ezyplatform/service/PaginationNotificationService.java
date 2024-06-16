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
import org.youngmonkeys.ezyplatform.model.SimpleNotificationModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescNotificationPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationFilter;
import org.youngmonkeys.ezyplatform.pagination.NotificationPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.NotificationPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationNotificationRepository;

public class PaginationNotificationService extends CommonPaginationService<
    SimpleNotificationModel,
    NotificationFilter,
    NotificationPaginationParameter,
    Long,
    Notification> {

    private final DefaultEntityToModelConverter entityToModelConverter;

    public PaginationNotificationService(
        PaginationNotificationRepository repository,
        DefaultEntityToModelConverter entityToModelConverter,
        NotificationPaginationParameterConverter notificationPaginationParameterConverter
    ) {
        super(repository, notificationPaginationParameterConverter);
        this.entityToModelConverter = entityToModelConverter;
    }

    @Override
    protected SimpleNotificationModel convertEntity(Notification entity) {
        return entityToModelConverter.toModel(entity);
    }

    @Override
    protected NotificationPaginationParameter defaultPaginationParameter() {
        return new IdDescNotificationPaginationParameter();
    }
}
