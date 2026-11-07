/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.pagination;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.DefaultAdminFilter;
import org.youngmonkeys.ezyplatform.pagination.DefaultUserFilter;

import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UniqueKeywordFilterTest {

    @Test
    public void userTextKeywordDoesNotCompareIdTest() {
        DefaultUserFilter filter = (DefaultUserFilter) new DefaultUserFilter.Builder()
            .uniqueKeyword("john")
            .build();

        Map<String, Object> parameters = filter.getParameters();

        Asserts.assertNull(filter.uniqueKeywordId);
        Asserts.assertFalse(filter.matchingCondition().contains("e.id ="));
        Asserts.assertTrue(filter.matchingCondition().contains("e.username = :uniqueKeyword"));
        Asserts.assertFalse(parameters.containsKey("uniqueKeywordId"));
        Asserts.assertEquals(parameters.get("uniqueKeyword"), "john");
    }

    @Test
    public void userNumericKeywordComparesIdAsLongTest() {
        DefaultUserFilter filter = (DefaultUserFilter) new DefaultUserFilter.Builder()
            .uniqueKeyword("123")
            .build();

        Map<String, Object> parameters = filter.getParameters();

        Asserts.assertEquals(filter.uniqueKeywordId, 123L);
        Asserts.assertTrue(filter.matchingCondition().contains("e.id = :uniqueKeywordId"));
        Asserts.assertTrue(filter.matchingCondition().contains("e.username = :uniqueKeyword"));
        Asserts.assertEquals(parameters.get("uniqueKeywordId"), 123L);
        Asserts.assertEquals(parameters.get("uniqueKeyword"), "123");
    }

    @Test
    public void userNumberPrefixedTextKeywordDoesNotCompareIdTest() {
        DefaultUserFilter filter = (DefaultUserFilter) new DefaultUserFilter.Builder()
            .uniqueKeyword("123abc")
            .build();

        Asserts.assertNull(filter.uniqueKeywordId);
        Asserts.assertFalse(filter.matchingCondition().contains("e.id ="));
    }

    @Test
    public void adminTextKeywordDoesNotCompareIdTest() {
        DefaultAdminFilter filter = (DefaultAdminFilter) new DefaultAdminFilter.Builder()
            .uniqueKeyword("admin")
            .build();

        Asserts.assertNull(filter.uniqueKeywordId);
        Asserts.assertFalse(filter.matchingCondition().contains("e.id ="));
        Asserts.assertFalse(filter.getParameters().containsKey("uniqueKeywordId"));
    }

    @Test
    public void adminNumericKeywordComparesIdAsLongTest() {
        DefaultAdminFilter filter = (DefaultAdminFilter) new DefaultAdminFilter.Builder()
            .uniqueKeyword("7")
            .build();

        Asserts.assertEquals(filter.uniqueKeywordId, 7L);
        Asserts.assertTrue(filter.matchingCondition().contains("e.id = :uniqueKeywordId"));
        Asserts.assertEquals(filter.getParameters().get("uniqueKeywordId"), 7L);
    }
}
