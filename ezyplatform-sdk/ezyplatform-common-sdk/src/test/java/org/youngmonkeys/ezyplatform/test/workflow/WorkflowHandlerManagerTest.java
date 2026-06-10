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

package org.youngmonkeys.ezyplatform.test.workflow;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.WorkflowHandlerSchema;
import org.youngmonkeys.ezyplatform.workflow.WorkflowHandler;
import org.youngmonkeys.ezyplatform.workflow.WorkflowHandlerManager;
import org.youngmonkeys.ezyplatform.workflow.WorkflowHandlerSchemaFetcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkflowHandlerManagerTest {

    @Test
    public void shouldGetWorkflowHandlerByName() {
        // given
        TestWorkflowHandler sendEmailHandler = new TestWorkflowHandler(
            "send-email"
        );
        TestWorkflowHandler sendSmsHandler = new TestWorkflowHandler(
            "send-sms"
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Arrays.asList(sendEmailHandler, sendSmsHandler));
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Collections.emptyList());

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        WorkflowHandler actualHandler = manager.getWorkflowHandlerByName(
            "send-email"
        );
        WorkflowHandler missingHandler = manager.getWorkflowHandlerByName(
            "missing"
        );

        // then
        Asserts.assertEquals(actualHandler, sendEmailHandler);
        Asserts.assertNull(missingHandler);
    }

    @Test
    public void shouldGetWorkflowHandlerSchemaFetcherByWorkflowName() {
        // given
        TestWorkflowHandlerSchemaFetcher emailFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Send email");
        TestWorkflowHandlerSchemaFetcher smsFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-sms", "Send sms");

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Arrays.asList(emailFetcher, smsFetcher));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        WorkflowHandlerSchemaFetcher actualFetcher =
            manager.getWorkflowHandlerSchemaFetcherByWorkflowName("send-email");
        WorkflowHandlerSchemaFetcher missingFetcher =
            manager.getWorkflowHandlerSchemaFetcherByWorkflowName("missing");

        // then
        Asserts.assertEquals(actualFetcher, emailFetcher);
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "Send email"
        );
        Asserts.assertNull(missingFetcher);
    }

    @Test
    public void shouldGetWorkflowHandlerSchemaFetchers() {
        // given
        TestWorkflowHandlerSchemaFetcher emailFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Send email");
        TestWorkflowHandlerSchemaFetcher smsFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-sms", "Send sms");

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Arrays.asList(emailFetcher, smsFetcher));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        List<WorkflowHandlerSchemaFetcher> actualFetchers =
            manager.getWorkflowHandlerSchemaFetchers();

        // then
        Asserts.assertEquals(actualFetchers.size(), 2);
        Asserts.assertTrue(actualFetchers.contains(emailFetcher));
        Asserts.assertTrue(actualFetchers.contains(smsFetcher));
    }

    @Test
    public void shouldGetWorkflowHandlerSchemaFetcherMap() {
        // given
        TestWorkflowHandlerSchemaFetcher emailFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Send email");
        TestWorkflowHandlerSchemaFetcher smsFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-sms", "Send sms");

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Arrays.asList(emailFetcher, smsFetcher));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        Map<String, WorkflowHandlerSchemaFetcher> actualMap =
            manager.getWorkflowHandlerSchemaFetcherMap();

        // then
        Asserts.assertEquals(actualMap.size(), 2);
        Asserts.assertEquals(actualMap.get("send-email"), emailFetcher);
        Asserts.assertEquals(actualMap.get("send-sms"), smsFetcher);
    }

    @Test
    public void shouldGetSortedWorkflowHandlerNames() {
        // given
        TestWorkflowHandler smsHandler =
            new TestWorkflowHandler("send-sms");
        TestWorkflowHandler emailHandler =
            new TestWorkflowHandler("send-email");
        TestWorkflowHandler pushHandler =
            new TestWorkflowHandler("send-push");

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Arrays.asList(smsHandler, emailHandler, pushHandler));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        List<String> actualNames = manager.getSortedWorkflowHandlerNames();

        // then
        Asserts.assertEquals(actualNames.size(), 3);
        Asserts.assertEquals(actualNames.get(0), "send-email");
        Asserts.assertEquals(actualNames.get(1), "send-push");
        Asserts.assertEquals(actualNames.get(2), "send-sms");
    }

    @Test
    public void shouldGetSortedWorkflowHandlerNamesWhenEmpty() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Collections.emptyList());

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        List<String> actualNames = manager.getSortedWorkflowHandlerNames();

        // then
        Asserts.assertEquals(actualNames.size(), 0);
    }

    @Test
    public void shouldOverrideLowerPriorityHandlerWithHigherPriority() {
        // given
        TestWorkflowHandler defaultHandler = new TestWorkflowHandler(
            "send-email",
            0
        );
        TestWorkflowHandler customHandler = new TestWorkflowHandler(
            "send-email",
            1
        );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Arrays.asList(defaultHandler, customHandler));
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Collections.emptyList());

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        WorkflowHandler actualHandler = manager.getWorkflowHandlerByName(
            "send-email"
        );

        // then
        Asserts.assertEquals(actualHandler, customHandler);
    }

    @Test
    public void shouldOverrideLowerPriorityFetcherWithHigherPriority() {
        // given
        TestWorkflowHandlerSchemaFetcher defaultFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Default", 0);
        TestWorkflowHandlerSchemaFetcher customFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Custom", 1);

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Arrays.asList(defaultFetcher, customFetcher));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        WorkflowHandlerSchemaFetcher actualFetcher =
            manager.getWorkflowHandlerSchemaFetcherByWorkflowName("send-email");

        // then
        Asserts.assertEquals(actualFetcher, customFetcher);
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "Custom"
        );
    }

    @Test
    public void shouldInitializeHandlersAndFetchersOnlyOnce() {
        // given
        TestWorkflowHandler sendEmailHandler = new TestWorkflowHandler(
            "send-email"
        );
        TestWorkflowHandlerSchemaFetcher emailFetcher =
            new TestWorkflowHandlerSchemaFetcher("send-email", "Send email");

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(WorkflowHandler.class))
            .thenReturn(Collections.singletonList(sendEmailHandler));
        when(singletonFactory.getSingletonsOf(WorkflowHandlerSchemaFetcher.class))
            .thenReturn(Collections.singletonList(emailFetcher));

        WorkflowHandlerManager manager = new WorkflowHandlerManager(
            singletonFactory
        );

        // when
        manager.getWorkflowHandlerByName("send-email");
        manager.getWorkflowHandlerByName("send-email");
        manager.getWorkflowHandlerSchemaFetcherByWorkflowName("send-email");
        manager.getWorkflowHandlerSchemaFetchers();
        manager.getWorkflowHandlerSchemaFetcherMap();
        manager.getSortedWorkflowHandlerNames();

        // then
        verify(singletonFactory, times(1))
            .getSingletonsOf(WorkflowHandler.class);
        verify(singletonFactory, times(1))
            .getSingletonsOf(WorkflowHandlerSchemaFetcher.class);
    }

    @SuppressWarnings("rawtypes")
    private static final class TestWorkflowHandler implements WorkflowHandler {

        private final String name;
        private final int priority;

        private TestWorkflowHandler(String name) {
            this(name, 0);
        }

        private TestWorkflowHandler(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public Object handle(Object input) {
            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    private static final class TestWorkflowHandlerSchemaFetcher
        implements WorkflowHandlerSchemaFetcher {

        private final String workflowName;
        private final WorkflowHandlerSchema schema;
        private final int priority;

        private TestWorkflowHandlerSchemaFetcher(
            String workflowName,
            String description
        ) {
            this(workflowName, description, 0);
        }

        private TestWorkflowHandlerSchemaFetcher(
            String workflowName,
            String description,
            int priority
        ) {
            this.workflowName = workflowName;
            this.priority = priority;
            this.schema = WorkflowHandlerSchema.builder()
                .description(description)
                .build();
        }

        @Override
        public WorkflowHandlerSchema getSchema() {
            return schema;
        }

        @Override
        public String getWorkflowName() {
            return workflowName;
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }
}
