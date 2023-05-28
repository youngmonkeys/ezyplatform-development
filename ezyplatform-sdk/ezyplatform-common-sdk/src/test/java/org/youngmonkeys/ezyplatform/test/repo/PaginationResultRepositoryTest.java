/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.repo;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezydata.jpa.EzyJpaDatabaseContext;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.pagination.CommonPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.CommonStorageFilter;
import org.youngmonkeys.ezyplatform.pagination.OffsetPaginationParameter;
import org.youngmonkeys.ezyplatform.repo.PaginationResultRepository;
import org.youngmonkeys.ezyplatform.result.IdResult;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PaginationResultRepositoryTest {

    private Query query;
    private EntityManager entityManager;
    private EzyJpaDatabaseContext databaseContext;

    @BeforeMethod
    public void setup() {
        query = mock(Query.class);
        entityManager = mock(EntityManager.class);
        databaseContext = mock(EzyJpaDatabaseContext.class);
        when(databaseContext.createEntityManager()).thenReturn(entityManager);
    }

    @AfterMethod
    public void verifyAll() {
        verify(databaseContext, times(1)).createEntityManager();
        verify(entityManager, times(1)).close();
        verifyNoMoreInteractions(
            query,
            databaseContext,
            entityManager
        );
    }

    @Test
    public void findFirstElementsTest() {
        // given
        TestPaginationResultRepository instance = new TestPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        // when
        List<IdResult> actual = instance.findFirstElements(
            filter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e"
        );
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(limit);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void findNextElementsTest() {
        // given
        TestPaginationResultRepository instance = new TestPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        TestPaginationParameter paginationParameter = new TestPaginationParameter();

        // when
        List<IdResult> actual = instance.findNextElements(
            filter,
            paginationParameter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e WHERE (e.id < :id) ORDER BY e.id DESC"
        );
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(limit);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void findPreviousElementsTest() {
        // given
        TestPaginationResultRepository instance = new TestPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        TestPaginationParameter paginationParameter = new TestPaginationParameter();

        // when
        List<IdResult> actual = instance.findPreviousElements(
            filter,
            paginationParameter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e WHERE (e.id > :id) ORDER BY e.id ASC"
        );
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(limit);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void findLastElementsTest() {
        // given
        TestPaginationResultRepository instance = new TestPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        // when
        List<IdResult> actual = instance.findLastElements(
            filter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e"
        );
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(limit);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void findNextElementsWithNegativeOffsetTest() {
        // given
        TestOffsetPaginationResultRepository instance = new TestOffsetPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        TestOffsetPaginationParameter paginationParameter = new TestOffsetPaginationParameter(
            -1
        );

        // when
        List<IdResult> actual = instance.findNextElements(
            filter,
            paginationParameter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e ORDER BY e.id DESC"
        );
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(limit - 1);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void findNextElementsWithPositiveOffsetTest() {
        // given
        TestOffsetPaginationResultRepository instance = new TestOffsetPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        int limit = RandomUtil.randomSmallInt();
        Query query = mock(Query.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(limit)).thenReturn(query);
        List<Long> ids = RandomUtil.randomShortList(Long.class);
        when(query.getResultList()).thenReturn(ids);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);
        List<IdResult> idResults = newArrayList(ids, id -> {
            IdResult result = new IdResult();
            result.setId(id);
            return result;
        });
        when(
            databaseContext.deserializeResultList(
                ids,
                IdResult.class
            )
        ).thenReturn(idResults);

        TestOffsetPaginationParameter paginationParameter = new TestOffsetPaginationParameter(
            1
        );

        // when
        List<IdResult> actual = instance.findNextElements(
            filter,
            paginationParameter,
            limit
        );

        // then
        Asserts.assertEquals(actual, idResults);

        verify(entityManager, times(1)).createQuery(
            "SELECT e FROM User e ORDER BY e.id DESC"
        );
        verify(query, times(1)).setFirstResult(1);
        verify(query, times(1)).setMaxResults(limit);

        verify(databaseContext, times(1)).deserializeResultList(
            ids,
            IdResult.class
        );
    }

    @Test
    public void countElementsTest() {
        // given
        TestPaginationResultRepository instance = new TestPaginationResultRepository();
        instance.setDatabaseContext(databaseContext);
        TestFilter filter = new TestFilter();
        filter.username = RandomUtil.randomShortAlphabetString();
        long count = RandomUtil.randomLong();
        Query query = mock(Query.class);
        when(query.setParameter("username", filter.username)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(count);
        when(
            entityManager.createQuery(anyString())
        ).thenReturn(query);

        // when
        long actual = instance.countElements(filter);

        // then
        Asserts.assertEquals(actual, count);

        verify(entityManager, times(1)).createQuery(
            "SELECT COUNT(e) FROM User e WHERE username = :username"
        );
        verify(query, times(1)).setParameter("username", filter.username);
        verify(query, times(1)).getSingleResult();
    }

    private static class TestPaginationResultRepository
        extends PaginationResultRepository<
        TestFilter,
        TestPaginationParameter,
        Long,
        User,
        IdResult
        > {}

    private static class TestOffsetPaginationResultRepository
        extends PaginationResultRepository<
        TestFilter,
        TestOffsetPaginationParameter,
        Long,
        User,
        IdResult
        > {}

    @Getter
    @Setter
    private static class TestFilter implements CommonStorageFilter {
        private String username;

        @Override
        public String matchingCondition() {
            EzyQueryConditionBuilder builder = new EzyQueryConditionBuilder();
            if (username != null) {
                builder.append("username = :username");
            }
            return builder.toString();
        }
    }

    private static class TestPaginationParameter implements CommonPaginationParameter {

        @Override
        public String paginationCondition(boolean nextPage) {
            return nextPage ? "e.id < :id" : "e.id > :id";
        }

        @Override
        public String orderBy(boolean nextPage) {
            return nextPage ? "e.id DESC" : "e.id ASC";
        }

        @Override
        public String sortOrder() {
            return "ID_DESC";
        }
    }

    private static class TestOffsetPaginationParameter extends OffsetPaginationParameter {

        public TestOffsetPaginationParameter(long offset) {
            super(offset, "e.id DESC");
        }
    }
}
