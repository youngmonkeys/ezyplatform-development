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
import org.youngmonkeys.ezyplatform.entity.AdminMeta;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class AdminMetaTransactionalRepository
    extends EzyJpaRepository<Long, AdminMeta> {

    @SuppressWarnings("unchecked")
    public void saveAdminMetaUniqueKey(
        long adminId,
        String metaKey,
        String metaValue
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<AdminMeta> entities = entityManager.createQuery(
                    "SELECT e FROM AdminMeta e " +
                        "WHERE e.adminId = ?0 AND metaKey = ?1"
                )
                    .setParameter(0, adminId)
                    .setParameter(1, metaKey)
                    .getResultList();
                AdminMeta entity = EzyLists.first(entities);
                if (entity == null) {
                    entity = new AdminMeta();
                }
                entity.setAdminId(adminId);
                entity.setMetaKey(metaKey);
                entity.setMetaValue(metaValue);
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
}
