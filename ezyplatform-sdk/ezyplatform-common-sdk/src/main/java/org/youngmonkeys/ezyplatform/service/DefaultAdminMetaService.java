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
import org.youngmonkeys.ezyplatform.entity.AdminMeta;
import org.youngmonkeys.ezyplatform.repo.AdminMetaRepository;
import org.youngmonkeys.ezyplatform.repo.AdminMetaTransactionalRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

@AllArgsConstructor
public class DefaultAdminMetaService implements AdminMetaService {

    private final AdminMetaRepository adminMetaRepository;
    private final AdminMetaTransactionalRepository adminMetaTransactionalRepository;

    @Override
    public void saveAdminMeta(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        AdminMeta entity = new AdminMeta();
        entity.setAdminId(adminId);
        entity.setMetaKey(metaKey);
        entity.setMetaValue(metaValue);
        entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
        adminMetaRepository.save(entity);
    }

    @Override
    public void saveAdminMeta(
        long adminId,
        String metaKey,
        List<String> metaValues
    ) {
        List<AdminMeta> entities = newArrayList(
            metaValues,
            metaValue -> {
                AdminMeta entity = new AdminMeta();
                entity.setAdminId(adminId);
                entity.setMetaKey(metaKey);
                entity.setMetaValue(metaValue);
                entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
                return entity;
            }
        );
        adminMetaRepository.save(entities);
    }

    @Override
    public void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        String metaValue,
        String metaTextValue
    ) {
        adminMetaTransactionalRepository.saveAdminMetaUniqueKey(
            adminId,
            metaKey,
            metaValue,
            metaTextValue
        );
    }

    @Override
    public void saveAdminMetaIfAbsent(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        adminMetaTransactionalRepository.saveAdminMetaUniqueKeyValue(
            adminId,
            metaKey,
            metaValue
        );
    }

    @Override
    public BigDecimal increaseAdminMetaValue(
        long adminId,
        String metaKey,
        BigDecimal value
    ) {
        return adminMetaTransactionalRepository.increaseMetaValue(
            adminId,
            metaKey,
            value
        );
    }

    @Override
    public boolean containsAdminMeta(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        return adminMetaRepository.findByAdminIdAndMetaKeyAndMetaValue(
            adminId,
            metaKey,
            metaValue
        ).isPresent();
    }

    @Override
    public long getAdminIdByMeta(
        String metaKey,
        String metaValue
    ) {
        return adminMetaRepository.findByMetaKeyAndMetaValue(
            metaKey,
            metaValue
        )
            .map(AdminMeta::getAdminId)
            .orElse(0L);
    }

    @Override
    public String getMetaValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        return adminMetaRepository.findByAdminIdAndMetaKey(
            adminId,
            metaKey
        )
            .map(AdminMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        return adminMetaRepository.findByAdminIdAndMetaKeyOrderByIdDesc(
            adminId,
            metaKey
        )
            .map(AdminMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public String getMetaTextValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        return adminMetaRepository.findByAdminIdAndMetaKey(
                adminId,
                metaKey
            )
            .map(AdminMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public String getLatestMetaTextValueByAdminIdAndMetaKey(
        long adminId,
        String metaKey
    ) {
        return adminMetaRepository.findByAdminIdAndMetaKeyOrderByIdDesc(
                adminId,
                metaKey
            )
            .map(AdminMeta::getMetaTextValue)
            .orElse(null);
    }

    @Override
    public List<String> getMetaValuesByAdminIdAndMetaKey(
        long adminId,
        String metaKey,
        int limit
    ) {
        return newArrayList(
            adminMetaRepository.findByAdminIdAndMetaKey(
                adminId,
                metaKey,
                Next.limit(limit)
            ),
            AdminMeta::getMetaValue
        );
    }

    @Override
    public Map<String, String> getAdminMetaValues(long adminId) {
        return adminMetaRepository.findByAdminId(
            adminId
        )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    AdminMeta::getMetaKey,
                    AdminMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, Long> getAdminIdMapByMetaValues(
        String metaKey,
        Collection<String> metaValues
    ) {
        if (metaValues.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminMetaRepository.findByMetaKeyAndMetaValueIn(
                metaKey,
                metaValues
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    AdminMeta::getMetaValue,
                    AdminMeta::getAdminId,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getAdminMetaValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    ) {
        if (adminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminMetaRepository.findByAdminIdInAndMetaKey(
                adminIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    AdminMeta::getAdminId,
                    AdminMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<String, String> getAdminMetaValueMapByAdminIdAndMetaKeys(
        long adminId,
        Collection<String> metaKeys
    ) {
        if (metaKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminMetaRepository.findByAdminIdAndMetaKeyIn(
                adminId,
                metaKeys
            )
            .stream()
            .filter(it -> it.getMetaValue() != null)
            .collect(
                Collectors.toMap(
                    AdminMeta::getMetaKey,
                    AdminMeta::getMetaValue,
                    (o, n) -> n
                )
            );
    }

    @Override
    public Map<Long, String> getAdminMetaTextValueMapByAdminIds(
        Collection<Long> adminIds,
        String metaKey
    ) {
        if (adminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adminMetaRepository.findByAdminIdInAndMetaKey(
                adminIds,
                metaKey
            )
            .stream()
            .filter(it -> it.getMetaTextValue() != null)
            .collect(
                Collectors.toMap(
                    AdminMeta::getAdminId,
                    AdminMeta::getMetaTextValue,
                    (o, n) -> n
                )
            );
    }
}
