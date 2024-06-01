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
import org.youngmonkeys.ezyplatform.entity.DataIndex;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyLists.first;

public class DataIndexTransactionalRepository
    extends EzyJpaRepository<Long, DataIndex> {

    @SuppressWarnings("unchecked")
    public void saveDataIndex(DataIndex dataIndex) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                List<DataIndex> entities = entityManager.createQuery(
                    "SELECT e FROM DataIndex e " +
                        "WHERE e.dataType = ?0 " +
                        "AND e.dataId = ?1 " +
                        "AND e.keyword = ?2"
                )
                    .setParameter(0, dataIndex.getDataType())
                    .setParameter(1, dataIndex.getDataId())
                    .setParameter(2, dataIndex.getKeyword())
                    .setMaxResults(1)
                    .getResultList();
                DataIndex entity = first(entities);
                if (entity != null) {
                    dataIndex.setId(entity.getId());
                }
                entityManager.merge(dataIndex);
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
    public void saveDataIndices(List<DataIndex> dataIndices) {
        EntityManager entityManager = databaseContext.createEntityManager();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                for (DataIndex dataIndex : dataIndices) {
                    List<DataIndex> entities = entityManager.createQuery(
                            "SELECT e FROM DataIndex e " +
                                "WHERE e.dataType = ?0 " +
                                "AND e.dataId = ?1 " +
                                "AND e.keyword = ?2"
                        )
                        .setParameter(0, dataIndex.getDataType())
                        .setParameter(1, dataIndex.getDataId())
                        .setParameter(2, dataIndex.getKeyword())
                        .setMaxResults(1)
                        .getResultList();
                    DataIndex entity = first(entities);
                    if (entity != null) {
                        dataIndex.setId(entity.getId());
                    }
                    entityManager.merge(dataIndex);
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
    protected Class<DataIndex> getEntityType() {
        return DataIndex.class;
    }
}
