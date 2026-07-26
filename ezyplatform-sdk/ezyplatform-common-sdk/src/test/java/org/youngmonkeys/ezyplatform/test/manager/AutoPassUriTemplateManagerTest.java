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

package org.youngmonkeys.ezyplatform.test.manager;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.manager.AutoPassUriTemplateManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AutoPassUriTemplateManagerTest {

    private AutoPassUriTemplateManager instance;

    @BeforeMethod
    public void setup() {
        instance = new AutoPassUriTemplateManager();
    }

    @Test
    public void addAutoPassUriTemplatesByVarargsTest() {
        // given
        // when
        instance.addAutoPassUriTemplates("/login", "/logout");

        // then
        Asserts.assertTrue(instance.containsUriTemplate("/login"));
        Asserts.assertTrue(instance.containsUriTemplate("/logout"));
        Asserts.assertEquals(
            instance.getAutoPassUriTemplates(),
            Arrays.asList("/login", "/logout"),
            false
        );
    }

    @Test
    public void addAutoPassUriTemplatesByCollectionTest() {
        // given
        List<String> uris = Arrays.asList("/health-check", "/favicon.ico");

        // when
        instance.addAutoPassUriTemplates(uris);

        // then
        Asserts.assertTrue(instance.containsUriTemplate("/health-check"));
        Asserts.assertTrue(instance.containsUriTemplate("/favicon.ico"));
        Asserts.assertEquals(
            instance.getAutoPassUriTemplates(),
            uris,
            false
        );
    }

    @Test
    public void containsUriReturnsFalseWhenUriTemplateNotAddedTest() {
        // given
        instance.addAutoPassUriTemplates("/login");

        // when
        boolean contains = instance.containsUriTemplate("/not-added");

        // then
        Asserts.assertFalse(contains);
    }

    @Test
    public void addAutoPassUriTemplatesWithDuplicateUrisDoesNotDuplicateTest() {
        // given
        instance.addAutoPassUriTemplates("/login");

        // when
        instance.addAutoPassUriTemplates("/login");

        // then
        Asserts.assertEquals(
            instance.getAutoPassUriTemplates(),
            Collections.singletonList("/login"),
            false
        );
    }

    @Test
    public void getAutoPassUriTemplatesReturnsEmptyListByDefaultTest() {
        // given
        // when
        List<String> uris = instance.getAutoPassUriTemplates();

        // then
        Asserts.assertEquals(uris, Collections.emptyList(), false);
    }
}
