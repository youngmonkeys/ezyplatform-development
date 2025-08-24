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

import com.tvd12.ezyfox.util.Next;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.UserMeta;
import org.youngmonkeys.ezyplatform.model.UserMetaModel;
import org.youngmonkeys.ezyplatform.repo.UserMetaRepository;
import org.youngmonkeys.ezyplatform.repo.UserMetaTransactionalRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

@AllArgsConstructor
public class DefaultUserMetaService implements UserMetaService {

    private final UserMetaRepository userMetaRepository;
    private final UserMetaTransactionalRepository userMetaTransactionalRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;

    @Override
    public void saveUserMeta(
        long userId,
        String metaKey,
        String metaValue
    ) {
        UserMeta entity = new UserMeta();
        entity.setUserId(userId);
        entity.setMetaKey(metaKey);
        entity.setMetaValue(metaValue);
        entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
        userMetaRepository.save(entity);
    }

    @Override
    public void saveUserMeta(
        long userId,
        String metaKey,
        List<String> metaValues
    ) {
        List<UserMeta> entities = newArrayList(
            metaValues,
            metaValue -> {
                UserMeta entity = new UserMeta();
                entity.setUserId(userId);
                entity.setMetaKey(metaKey);
                entity.setMetaValue(metaValue);
                entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
                return entity;
            }
        );
        userMetaRepository.save(entities);
    }

    @Override
    public void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue,
        String metaTextValue
    ) {
        userMetaTransactionalRepository.saveUserMetaUniqueKey(
            userId,
            metaKey,
            metaValue,
            metaTextValue
        );
    }

    @Override
    public void saveUserMetaIfAbsent(
        long userId,
        String metaKey,
        String metaValue
    ) {
        userMetaTransactionalRepository.saveUserMetaUniqueKeyValue(
            userId,
            metaKey,
            metaValue
        );
    }

    @Override
    public BigDecimal increaseUserMetaValue(
        long userId,
        String metaKey,
        BigDecimal value
    ) {
        return userMetaTransactionalRepository.increaseMetaValue(
            userId,
            metaKey,
            value
        );
    }
    
    @Override
    public void deleteUserMetaById(long id) {
        userMetaRepository.delete(id);
    }

    @Override
    public void deleteUserMetaByUserId(long userId) {
        userMetaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteUserMetaByUserIds(
        Collection<Long> userIds
    ) {
        if (!userIds.isEmpty()) {
            userMetaRepository.deleteByUserIdIn(userIds);
        }
    }

    @Override
    public boolean containsUserMeta(
        long userId,
        String metaKey,
        String metaValue
    ) {
        return userMetaRepository.findByUserIdAndMetaKeyAndMetaValue(
            userId,
            metaKey,
            metaValue
        ).isPresent();
    }

    @Override
    public long getUserIdByMeta(
        String metaKey,
        String metaValue
    ) {
        return userMetaRepository.findByMetaKeyAndMetaValue(
            metaKey,
            metaValue
        )
            .map(UserMeta::getUserId)
            .orElse(0L);
    }

    @Override
    public String getMetaValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdAndMetaKey(
            userId,
            metaKey
        )
            .map(UserMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdAndMetaKeyOrderByIdDesc(
                userId,
                metaKey
            )
            .map(UserMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getMetaTextValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdAndMetaKey(
                userId,
                metaKey
            )
            .map(UserMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaTextValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdAndMetaKeyOrderByIdDesc(
                userId,
                metaKey
            )
            .map(UserMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public List<String> getMetaValuesByUserIdAndMetaKey(
        long userId,
        String metaKey,
        int limit
    ) {
        return newArrayList(
            userMetaRepository.findByUserIdAndMetaKey(
                userId,
                metaKey,
                Next.limit(limit)
            ),
            UserMeta::getMetaValue
        );
    }

    @Override
    public Map<String, String> getUserMetaValues(long userId) {
        return userMetaRepository.findByUserId(
            userId
        )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, String> getUserMetaTextValues(
        long userId
    ) {
        return userMetaRepository.findByUserId(
                userId
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, Map<String, UserMetaModel>> getUserMetaValueMapsByUserIds(
        Collection<Long> userIds
    ) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository
            .findByUserIdIn(userIds)
            .stream()
            .collect(
                Collectors.groupingBy(
                    UserMeta::getUserId,
                    Collectors.toMap(
                        UserMeta::getMetaKey,
                        entityToModelConverter::toModel,
                        (o, n) -> n
                    )
                )
            );
    }

    @Override
    public Map<String, Long> getUserIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    ) {
        if (metaValues.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository.findByMetaKeyAndMetaValueIn(
            metaKey,
            metaValues
        )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaValue,
                    UserMeta::getUserId,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    ) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository.findByUserIdInAndMetaKey(
                userIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getUserId,
                    UserMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, String> getUserMetaValueMapByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    ) {
        if (metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository.findByUserIdAndMetaKeyIn(
                userId,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }
    
    public Map<String, String> getUserMetaTextValueMapByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    ) {
        if (metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository.findByUserIdAndMetaKeyIn(
                userId,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getUserMetaTextValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    ) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMetaRepository.findByUserIdInAndMetaKey(
                userIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    UserMeta::getUserId,
                    UserMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public List<UserMetaModel> getMetaListByUserIdAndMetaKeys(
        long userId,
        Collection<String> metaKeys
    ) {
        return newArrayList(
            userMetaRepository.findByUserIdAndMetaKeyIn(
                userId,
                metaKeys
            ),
            entityToModelConverter::toModel
        );
    }
}
