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

package org.youngmonkeys.ezyplatform.test.manager;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.ViewSchema;
import org.youngmonkeys.ezyplatform.fetcher.ViewMultipleSchemaFetcher;
import org.youngmonkeys.ezyplatform.fetcher.ViewSchemaFetcher;
import org.youngmonkeys.ezyplatform.manager.ViewSchemaFetcherManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ViewSchemaFetcherManagerTest {

    @Test
    public void shouldGetFetcherFromSingletonFactory() {
        // given
        TestViewSchemaFetcher homeFetcher = new TestViewSchemaFetcher(
            "/home",
            "home",
            "Home page"
        );
        TestViewSchemaFetcher contactFetcher = new TestViewSchemaFetcher(
            "/contact",
            "contact",
            "Contact page"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(ViewSchemaFetcher.class))
            .thenReturn(Arrays.asList(homeFetcher, contactFetcher));
        when(singletonFactory.getSingletonsOf(ViewMultipleSchemaFetcher.class))
            .thenReturn(Collections.emptyList());

        ViewSchemaFetcherManager manager = new ViewSchemaFetcherManager(
            singletonFactory
        );

        // when
        ViewSchemaFetcher actualFetcher = manager.getViewSchemaByUri("/home");
        ViewSchemaFetcher missingFetcher = manager.getViewSchemaByUri("/missing");

        // then
        Asserts.assertEquals(actualFetcher, homeFetcher);
        Asserts.assertEquals(actualFetcher.getSchema().getTemplate(), "home");
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "Home page"
        );
        Asserts.assertNull(missingFetcher);
    }

    @Test
    public void shouldGetFetcherFromMultipleSchemaFetcherAndReturnAllFetchers() {
        // given
        TestViewSchemaFetcher directFetcher = new TestViewSchemaFetcher(
            "/direct",
            "direct",
            "Direct page"
        );
        TestViewSchemaFetcher firstGroupFetcher = new TestViewSchemaFetcher(
            "/group/one",
            "group-one",
            "Group page one"
        );
        TestViewSchemaFetcher secondGroupFetcher = new TestViewSchemaFetcher(
            "/group/two",
            "group-two",
            "Group page two"
        );
        TestViewMultipleSchemaFetcher multipleSchemaFetcher =
            new TestViewMultipleSchemaFetcher(
                firstGroupFetcher,
                secondGroupFetcher
            );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(ViewSchemaFetcher.class))
            .thenReturn(Collections.singletonList(directFetcher));
        when(singletonFactory.getSingletonsOf(ViewMultipleSchemaFetcher.class))
            .thenReturn(Collections.singletonList(multipleSchemaFetcher));

        ViewSchemaFetcherManager manager = new ViewSchemaFetcherManager(
            singletonFactory
        );

        // when
        ViewSchemaFetcher actualGroupFetcher =
            manager.getViewSchemaByUri("/group/one");
        List<ViewSchemaFetcher> actualFetchers =
            manager.getViewSchemaFetchers();

        // then
        Asserts.assertEquals(actualGroupFetcher, firstGroupFetcher);
        Asserts.assertEquals(actualFetchers.size(), 3);
        Asserts.assertTrue(actualFetchers.contains(directFetcher));
        Asserts.assertTrue(actualFetchers.contains(firstGroupFetcher));
        Asserts.assertTrue(actualFetchers.contains(secondGroupFetcher));
    }

    @Test
    public void shouldUseMultipleSchemaFetcherWhenUriDuplicated() {
        // given
        TestViewSchemaFetcher directFetcher = new TestViewSchemaFetcher(
            "/profile",
            "profile",
            "Direct profile page"
        );
        TestViewSchemaFetcher multipleFetcher = new TestViewSchemaFetcher(
            "/profile",
            "custom-profile",
            "Custom profile page"
        );
        TestViewMultipleSchemaFetcher multipleSchemaFetcher =
            new TestViewMultipleSchemaFetcher(multipleFetcher);

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(ViewSchemaFetcher.class))
            .thenReturn(Collections.singletonList(directFetcher));
        when(singletonFactory.getSingletonsOf(ViewMultipleSchemaFetcher.class))
            .thenReturn(Collections.singletonList(multipleSchemaFetcher));

        ViewSchemaFetcherManager manager = new ViewSchemaFetcherManager(
            singletonFactory
        );

        // when
        ViewSchemaFetcher actualFetcher = manager.getViewSchemaByUri(
            "/profile"
        );

        // then
        Asserts.assertEquals(actualFetcher, multipleFetcher);
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "Custom profile page"
        );
    }

    @Test
    public void shouldInitializeFetchersOnlyOnce() {
        // given
        TestViewSchemaFetcher homeFetcher = new TestViewSchemaFetcher(
            "/home",
            "home",
            "Home page"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(ViewSchemaFetcher.class))
            .thenReturn(Collections.singletonList(homeFetcher));
        when(singletonFactory.getSingletonsOf(ViewMultipleSchemaFetcher.class))
            .thenReturn(Collections.emptyList());

        ViewSchemaFetcherManager manager = new ViewSchemaFetcherManager(
            singletonFactory
        );

        // when
        manager.getViewSchemaByUri("/home");
        manager.getViewSchemaFetchers();

        // then
        verify(singletonFactory, times(1))
            .getSingletonsOf(ViewSchemaFetcher.class);
        verify(singletonFactory, times(1))
            .getSingletonsOf(ViewMultipleSchemaFetcher.class);
    }

    private static final class TestViewSchemaFetcher
        implements ViewSchemaFetcher {

        private final String viewUri;
        private final ViewSchema schema;

        private TestViewSchemaFetcher(
            String viewUri,
            String template,
            String description
        ) {
            this.viewUri = viewUri;
            this.schema = ViewSchema.builder()
                .template(template)
                .description(description)
                .build();
        }

        @Override
        public ViewSchema getSchema() {
            return schema;
        }

        @Override
        public String getViewUri() {
            return viewUri;
        }
    }

    private static final class TestViewMultipleSchemaFetcher
        implements ViewMultipleSchemaFetcher {

        private final List<ViewSchemaFetcher> viewSchemaFetchers;

        private TestViewMultipleSchemaFetcher(
            ViewSchemaFetcher... viewSchemaFetchers
        ) {
            this.viewSchemaFetchers = Arrays.asList(viewSchemaFetchers);
        }

        @Override
        public List<ViewSchemaFetcher> getViewSchemaFetchers() {
            return viewSchemaFetchers;
        }
    }
}
