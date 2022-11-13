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
import org.youngmonkeys.ezyplatform.entity.UserMeta;
import org.youngmonkeys.ezyplatform.repo.UserMetaRepository;
import org.youngmonkeys.ezyplatform.repo.UserMetaTransactionalRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultUserMetaService implements UserMetaService {

    private final UserMetaRepository userMetaRepository;
    private final UserMetaTransactionalRepository userMetaTransactionalRepository;

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
                return entity;
            }
        );
        userMetaRepository.save(entities);
    }

    @Override
    public void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue
    ) {
        userMetaTransactionalRepository.saveUserMetaUniqueKey(
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
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaValue
                )
            );
    }

    @Override
    public Map<String, Long> getUserIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    ) {
        return userMetaRepository.findByMetaKeyAndMetaValueIn(
            metaKey,
            metaValues
        )
            .stream()
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaValue,
                    UserMeta::getUserId
                )
            );
    }

    @Override
    public Map<Long, String> getUserMetaValueMapByUserIds(
        Collection<Long> userIds,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdInAndMetaKey(
                userIds,
                metaKey
            )
            .stream()
            .collect(
                Collectors.toMap(
                    UserMeta::getUserId,
                    UserMeta::getMetaValue
                )
            );
    }
}
