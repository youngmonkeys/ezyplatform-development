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
import org.youngmonkeys.ezyplatform.entity.DataMeta;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.List;

import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

public class DataMetaTransactionalRepository
    extends EzyJpaRepository<Long, DataMeta> {

    @SuppressWarnings("unchecked")
    public void saveDataMetaUniqueKey(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue,
        String metaTextValue
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<DataMeta> entities = entityManager.createQuery(
                    "SELECT e FROM DataMeta e " +
                        "WHERE e.dataType = ?0 " +
                        "AND e.dataId = ?1 " +
                        "AND e.metaKey = ?2"
                )
                    .setParameter(0, dataType)
                    .setParameter(1, dataId)
                    .setParameter(2, metaKey)
                    .setMaxResults(1)
                    .getResultList();
                DataMeta entity = EzyLists.first(entities);
                if (entity == null) {
                    entity = new DataMeta();
                    entity.setDataType(dataType);
                    entity.setDataId(dataId);
                    entity.setMetaKey(metaKey);
                }
                entity.setMetaValue(metaValue);
                entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
                entity.setMetaTextValue(metaTextValue);
                entityManager.merge(entity);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    public void saveDataMetaUniqueKeyValue(
        String dataType,
        long dataId,
        String metaKey,
        String metaValue
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<DataMeta> entities = entityManager.createQuery(
                        "SELECT e FROM DataMeta e " +
                            "WHERE e.dataType = ?0 " +
                            "AND e.dataId = ?1 " +
                            "AND e.metaKey = ?2 " +
                            "AND e.metaValue = ?3"
                    )
                    .setParameter(0, dataType)
                    .setParameter(1, dataId)
                    .setParameter(2, metaKey)
                    .setParameter(3, metaValue)
                    .setMaxResults(1)
                    .getResultList();
                DataMeta entity = EzyLists.first(entities);
                if (entity == null) {
                    entity = new DataMeta();
                    entity.setDataType(dataType);
                    entity.setDataId(dataId);
                    entity.setMetaKey(metaKey);
                    entity.setMetaValue(metaValue);
                    entity.setMetaNumberValue(toBigIntegerOrZero(metaValue));
                    entityManager.merge(entity);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    public BigDecimal increaseMetaValue(
        String dataType,
        long dataId,
        String metaKey,
        BigDecimal value
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<DataMeta> entities = entityManager.createQuery(
                        "SELECT e FROM DataMeta e " +
                            "WHERE e.dataType = ?0 " +
                            "AND e.dataId = ?1 " +
                            "AND e.metaKey = ?2"
                    )
                    .setParameter(0, dataType)
                    .setParameter(1, dataId)
                    .setParameter(2, metaKey)
                    .setMaxResults(1)
                    .getResultList();
                DataMeta entity = EzyLists.first(entities);
                BigDecimal currentValue = BigDecimal.ZERO;
                if (entity == null) {
                    entity = new DataMeta();
                    entity.setDataType(dataType);
                    entity.setDataId(dataId);
                    entity.setMetaKey(metaKey);
                } else {
                    currentValue = new BigDecimal(entity.getMetaValue());
                }
                BigDecimal newValue = currentValue.add(value);
                entity.setMetaValue(newValue.toString());
                entity.setMetaNumberValue(newValue.toBigInteger());
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
    protected Class<DataMeta> getEntityType() {
        return DataMeta.class;
    }
}
