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
import org.youngmonkeys.ezyplatform.entity.UserKeyword;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyLists.first;

public class UserKeywordTransactionalRepository
    extends EzyJpaRepository<Long, UserKeyword> {

    @SuppressWarnings("unchecked")
    public void saveUserKeyword(UserKeyword userKeyword) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<UserKeyword> entities = entityManager.createQuery(
                    "SELECT e FROM UserKeyword e " +
                        "WHERE e.userId = ?0 " +
                        "AND e.keyword = ?1"
                )
                    .setParameter(0, userKeyword.getUserId())
                    .setParameter(1, userKeyword.getKeyword())
                    .setMaxResults(1)
                    .getResultList();
                UserKeyword entity = first(entities);
                if (entity != null) {
                    userKeyword.setId(entity.getId());
                    userKeyword.setCreatedAt(entity.getCreatedAt());
                }
                entityManager.merge(userKeyword);
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
    public void saveUserKeywords(List<UserKeyword> userKeywords) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                for (UserKeyword userKeyword : userKeywords) {
                    List<UserKeyword> entities = entityManager.createQuery(
                            "SELECT e FROM UserKeyword e " +
                                "WHERE e.userId = ?0 " +
                                "AND e.keyword = ?1"
                        )
                        .setParameter(0, userKeyword.getUserId())
                        .setParameter(1, userKeyword.getKeyword())
                        .setMaxResults(1)
                        .getResultList();
                    UserKeyword entity = first(entities);
                    if (entity != null) {
                        userKeyword.setId(entity.getId());
                        userKeyword.setCreatedAt(entity.getCreatedAt());
                    }
                    entityManager.merge(userKeyword);
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

    @Override
    protected Class<UserKeyword> getEntityType() {
        return UserKeyword.class;
    }
}
