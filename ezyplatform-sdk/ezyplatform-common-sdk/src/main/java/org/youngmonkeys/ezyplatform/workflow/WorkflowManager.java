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

package org.youngmonkeys.ezyplatform.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkflowManager {

    private final Map<String, Workflow> workflowByName =
        new ConcurrentHashMap<>();

    public void addWorkflow(
        Workflow workflow
    ) {
        workflowByName.put(workflow.getName(), workflow);
    }

    public void addWorkflows(
        List<Workflow> workflows
    ) {
        for (Workflow workflow : workflows) {
            workflowByName.put(workflow.getName(), workflow);
        }
    }

    public void removeWorkflow(
        String workflowName
    ) {
        workflowByName.remove(workflowName);
    }

    public Workflow getWorkflowByName(
        String workflowName
    ) {
        return workflowByName.get(workflowName);
    }

    public List<String> getWorkflowNames() {
        return new ArrayList<>(workflowByName.keySet());
    }

    public List<String> getSortedWorkflowNames() {
        return workflowByName
            .keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
