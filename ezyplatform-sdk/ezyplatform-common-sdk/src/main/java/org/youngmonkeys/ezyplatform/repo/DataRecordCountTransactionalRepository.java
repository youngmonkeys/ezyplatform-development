/*
 * Copyright 2025 youngmonkeys.org
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
import org.youngmonkeys.ezyplatform.entity.DataRecordCount;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.time.LocalDateTime;

public class DataRecordCountTransactionalRepository
    extends EzyJpaRepository<String, DataRecordCount> {

    public void incrementRecordCount(
        String dataType,
        long value
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            Query query = entityManager.createNativeQuery(
                "UPDATE ezy_data_record_counts e " +
                    "SET e.record_count = GREATEST(0, e.record_count + ?1) " +
                    "WHERE e.data_type = ?0"
            );
            query.setParameter(0, dataType);
            query.setParameter(1, value);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                query.executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    public void incrementRecordCount(
        String dataType,
        long value,
        long lastRecordId,
        LocalDateTime time
    ) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            Query query = entityManager.createNativeQuery(
                "UPDATE ezy_data_record_counts e " +
                    "SET e.record_count = GREATEST(0, e.record_count + ?1), " +
                    "e.last_record_id = CASE WHEN ?1 > 0 THEN ?2 ELSE e.last_record_id END, " +
                    "e.last_counted_at = ?3 " +
                    "WHERE e.data_type = ?0"
            );
            query.setParameter(0, dataType);
            query.setParameter(1, value);
            query.setParameter(2, lastRecordId);
            query.setParameter(3, time);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                query.executeUpdate();
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
    protected Class<DataRecordCount> getEntityType() {
        return DataRecordCount.class;
    }
}
