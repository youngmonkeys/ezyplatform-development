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
import org.youngmonkeys.ezyplatform.workflow.WorkflowManager;

import java.util.ArrayList;
import java.util.List;

public class WorkflowManagerTest {

    @Test
    public void addGetAndRemoveWorkflowTest() {
        // given
        WorkflowManager sut = new WorkflowManager();
        String workflowName = RandomUtil.randomShortAlphabetString();
        String displayName = RandomUtil.randomShortAlphabetString();
        Workflow workflow = new Workflow.Builder()
            .name(workflowName)
            .displayName(displayName)
            .build();

        // when
        sut.addWorkflow(workflow);

        // then
        Asserts.assertEquals(
            sut.getWorkflowByName(workflowName),
            workflow
        );
        Asserts.assertEquals(
            sut.getWorkflowByName(workflowName).getName(),
            workflowName
        );
        Asserts.assertEquals(
            sut.getWorkflowByName(workflowName).getDisplayName(),
            displayName
        );

        // when
        sut.removeWorkflow(workflowName);

        // then
        Asserts.assertNull(sut.getWorkflowByName(workflowName));
    }

    @Test
    public void addWorkflowsAndGetSortedNamesTest() {
        // given
        WorkflowManager sut = new WorkflowManager();
        String workflowName1 = "zeta";
        String workflowName2 = "alpha";
        Workflow workflow1 = new Workflow.Builder()
            .name(workflowName1)
            .displayName(RandomUtil.randomShortAlphabetString())
            .build();
        Workflow workflow2 = new Workflow.Builder()
            .name(workflowName2)
            .displayName(RandomUtil.randomShortAlphabetString())
            .build();
        List<Workflow> workflows = new ArrayList<>();
        workflows.add(workflow1);
        workflows.add(workflow2);

        // when
        sut.addWorkflows(workflows);

        // then
        Asserts.assertEquals(
            sut.getWorkflowNames().size(),
            2
        );
        Asserts.assertEquals(
            sut.getSortedWorkflowNames().size(),
            2
        );
        Asserts.assertEquals(
            sut.getSortedWorkflowNames().get(0),
            workflowName2
        );
        Asserts.assertEquals(
            sut.getSortedWorkflowNames().get(1),
            workflowName1
        );
    }
}
