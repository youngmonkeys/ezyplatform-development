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
import org.youngmonkeys.ezyplatform.entity.UserMeta;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.youngmonkeys.ezyplatform.util.Strings.toBigIntegerOrZero;

public class UserMetaTransactionalRepository
    extends EzyJpaRepository<Long, UserMeta> {

    @SuppressWarnings("unchecked")
    public void saveUserMetaUniqueKey(
        long userId,
        String metaKey,
        String metaValue,
        String metaTextValue
    ) {
        BigInteger metaNumberValue = toBigIntegerOrZero(metaValue);
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<UserMeta> entities = entityManager.createQuery(
                    "SELECT e FROM UserMeta e " +
                        "WHERE e.userId = ?0 " +
                        "AND e.metaKey = ?1"
                )
                    .setParameter(0, userId)
                    .setParameter(1, metaKey)
                    .setMaxResults(1)
                    .getResultList();
                UserMeta entity = EzyLists.first(entities);
                if (entity == null) {
                    entity = new UserMeta();
                    entity.setUserId(userId);
                    entity.setMetaKey(metaKey);
                }
                entity.setMetaValue(metaValue);
                entity.setMetaNumberValue(metaNumberValue);
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
    public void saveUserMetaUniqueKeyValue(
        long userId,
        String metaKey,
        String metaValue
    ) {
        BigInteger metaNumberValue = toBigIntegerOrZero(metaValue);
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<UserMeta> entities = entityManager.createQuery(
                        "SELECT e FROM UserMeta e " +
                            "WHERE e.userId = ?0 " +
                            "AND e.metaKey = ?1 " +
                            "AND e.metaValue = ?2"
                    )
                    .setParameter(0, userId)
                    .setParameter(1, metaKey)
                    .setParameter(2, metaValue)
                    .setMaxResults(1)
                    .getResultList();
                UserMeta entity = EzyLists.first(entities);
                if (entity == null) {
                    entity = new UserMeta();
                    entity.setUserId(userId);
                    entity.setMetaKey(metaKey);
                    entity.setMetaValue(metaValue);
                    entity.setMetaNumberValue(metaNumberValue);
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
        long userId,
        String metaKey,
        BigDecimal value
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<UserMeta> entities = entityManager.createQuery(
                        "SELECT e FROM UserMeta e " +
                            "WHERE e.userId = ?0 " +
                            "AND e.metaKey = ?1"
                    )
                    .setParameter(0, userId)
                    .setParameter(1, metaKey)
                    .setMaxResults(1)
                    .getResultList();
                UserMeta entity = EzyLists.first(entities);
                BigDecimal currentValue = BigDecimal.ZERO;
                if (entity == null) {
                    entity = new UserMeta();
                    entity.setUserId(userId);
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
    protected Class<UserMeta> getEntityType() {
        return UserMeta.class;
    }
}
