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
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.Notification;
import org.youngmonkeys.ezyplatform.entity.NotificationReceiver;
import org.youngmonkeys.ezyplatform.model.AddNotificationModel;
import org.youngmonkeys.ezyplatform.model.AddNotificationReceiverModel;
import org.youngmonkeys.ezyplatform.model.SimpleNotificationModel;
import org.youngmonkeys.ezyplatform.repo.NotificationReceiverRepository;
import org.youngmonkeys.ezyplatform.repo.NotificationRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationReceiverRepository notificationReceiverRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public long addNotification(
        AddNotificationModel model
    ) {
        Notification notification = modelToEntityConverter
            .toEntity(model);
        notificationRepository.save(notification);
        List<NotificationReceiver> notificationReceivers =
            modelToEntityConverter.toEntities(
                notification.getId(),
                model
            );
        if (!notificationReceivers.isEmpty()) {
            notificationReceiverRepository.save(notificationReceivers);
        }
        return notification.getId();
    }

    @Override
    public long addNotificationReceiver(
        AddNotificationReceiverModel model
    ) {
        NotificationReceiver entity = modelToEntityConverter
            .toEntity(model);
        notificationReceiverRepository.save(entity);
        return entity.getId();
    }

    @Override
    public Map<Long, SimpleNotificationModel> getNotificationMapByIds(
        Collection<Long> notificationIds
    ) {
        if (notificationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return notificationRepository.findListByIds(notificationIds)
            .stream()
            .collect(
                Collectors.toMap(
                    Notification::getId,
                    entityToModelConverter::toModel
                )
            );
    }
}
