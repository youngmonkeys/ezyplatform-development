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

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.workflow.Workflow;
import org.youngmonkeys.ezyplatform.workflow.WorkflowHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowTest {

    @Test
    public void runAndGetHandlersTest() {
        // given
        Workflow sut = new Workflow.Builder()
            .name(RandomUtil.randomShortAlphabetString())
            .displayName(RandomUtil.randomShortAlphabetString())
            .build();
        List<String> executedHandlerNames = new ArrayList<>();
        List<WorkflowHandler> handlers = new ArrayList<>();
        handlers.add(
            new TestWorkflowHandler("first", executedHandlerNames)
        );
        handlers.add(
            new TestWorkflowHandler("second", executedHandlerNames)
        );
        sut.saveHandlers(handlers);
        Map<String, Object> input = new HashMap<>();
        Map<String, Object> output = new HashMap<>();
        input.put("value", "hello");

        // when
        sut.run(input, output);
        List<WorkflowHandler> actualHandlers = sut.getHandlers();
        actualHandlers.clear();

        // then
        Asserts.assertEquals(
            executedHandlerNames.size(),
            2
        );
        Asserts.assertEquals(
            executedHandlerNames.get(0),
            "first"
        );
        Asserts.assertEquals(
            executedHandlerNames.get(1),
            "second"
        );
        Asserts.assertEquals(
            output.get("first"),
            "hello"
        );
        Asserts.assertEquals(
            output.get("second"),
            "hello"
        );
        Asserts.assertEquals(
            sut.getHandlers().size(),
            2
        );
    }

    @Test
    public void runWithoutHandlersTest() {
        // given
        Workflow sut = new Workflow.Builder()
            .name(RandomUtil.randomShortAlphabetString())
            .displayName(RandomUtil.randomShortAlphabetString())
            .build();
        Map<String, Object> input = Collections.emptyMap();
        Map<String, Object> output = new HashMap<>();

        // when
        sut.run(input, output);

        // then
        Asserts.assertEquals(output.size(), 0);
        Asserts.assertEquals(
            sut.getHandlers().size(),
            0
        );
    }

    @Test
    public void getWorkflowPropertiesTest() {
        // given
        String workflowName = RandomUtil.randomShortAlphabetString();
        String displayName = RandomUtil.randomShortAlphabetString();

        // when
        Workflow sut = new Workflow.Builder()
            .name(workflowName)
            .displayName(displayName)
            .build();

        // then
        Asserts.assertEquals(
            sut.getName(),
            workflowName
        );
        Asserts.assertEquals(
            sut.getDisplayName(),
            displayName
        );
    }

    private static final class TestWorkflowHandler
        implements WorkflowHandler {

        private final String name;
        private final List<String> executedHandlerNames;

        private TestWorkflowHandler(
            String name,
            List<String> executedHandlerNames
        ) {
            this.name = name;
            this.executedHandlerNames = executedHandlerNames;
        }

        @Override
        public void handle(
            Map<String, Object> input,
            Map<String, Object> output
        ) {
            executedHandlerNames.add(name);
            output.put(name, input.get("value"));
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
