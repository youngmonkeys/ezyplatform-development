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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.jpa.repository.EzyJpaRepository;
import com.tvd12.ezyfox.io.EzyLists;
import org.youngmonkeys.ezyplatform.entity.DataActionCount;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class DataActionCountTransactionalRepository
    extends EzyJpaRepository<Long, DataActionCount> {

    @SuppressWarnings("unchecked")
    public BigInteger increaseDataActionCount(
        String dataType,
        String actionType,
        long dataId,
        BigInteger value,
        LocalDateTime now
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<DataActionCount> entities = entityManager.createQuery(
                    "SELECT e FROM DataActionCount e " +
                        "WHERE " +
                        "e.dataType = ?0 AND " +
                        "e.actionType = ?1 AND dataId = ?2"
                )
                    .setParameter(0, dataType)
                    .setParameter(1, actionType)
                    .setParameter(2, dataId)
                    .setMaxResults(1)
                    .getResultList();
                DataActionCount entity = EzyLists.first(entities);
                BigInteger currentValue = BigInteger.ZERO;
                if (entity == null) {
                    entity = new DataActionCount();
                    entity.setDataType(dataType);
                    entity.setActionType(actionType);
                    entity.setDataId(dataId);
                    entity.setCreatedAt(now);
                } else {
                    currentValue = entity.getActionCount();
                }
                BigInteger newValue = currentValue.add(value);
                entity.setActionCount(newValue);
                entity.setUpdatedAt(now);
                entityManager.merge(entity);
                transaction.commit();
                return newValue;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    protected Class<DataActionCount> getEntityType() {
        return DataActionCount.class;
    }
}
