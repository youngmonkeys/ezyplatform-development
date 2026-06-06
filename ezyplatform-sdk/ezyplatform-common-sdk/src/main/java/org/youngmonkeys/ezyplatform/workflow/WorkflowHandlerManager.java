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

package org.youngmonkeys.ezyplatform.workflow;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WorkflowHandlerManager {

    private final EzyLazyInitializer<Map<String, WorkflowHandler>>
        workflowHandlersByName;
    private final EzyLazyInitializer<Map<String, WorkflowHandlerSchemaFetcher>>
        workflowHandlerSchemaFetcherByName;

    public WorkflowHandlerManager(
        EzySingletonFactory singletonFactory
    ) {
        this.workflowHandlersByName = new EzyLazyInitializer<>(() ->
            ((List<WorkflowHandler>) singletonFactory
                .getSingletonsOf(WorkflowHandler.class)
            )
                .stream()
                .sorted(Comparator.comparingInt(WorkflowHandler::getPriority))
                .collect(
                    Collectors.toMap(
                        WorkflowHandler::getName,
                        it -> it,
                        (o, n) -> n
                    )
                )
        );
        this.workflowHandlerSchemaFetcherByName = new EzyLazyInitializer<>(() ->
            ((List<WorkflowHandlerSchemaFetcher>) singletonFactory
                .getSingletonsOf(WorkflowHandlerSchemaFetcher.class)
            )
                .stream()
                .sorted(Comparator.comparingInt(WorkflowHandlerSchemaFetcher::getPriority))
                .collect(
                    Collectors.toMap(
                        WorkflowHandlerSchemaFetcher::getWorkflowName,
                        it -> it,
                        (o, n) -> n
                    )
                )
        );
    }

    public List<String> getSortedWorkflowHandlerNames() {
        return workflowHandlerSchemaFetcherByName
            .get()
            .keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public WorkflowHandler getWorkflowHandlerByName(
        String name
    ) {
        return workflowHandlersByName
            .get()
            .get(name);
    }

    public WorkflowHandlerSchemaFetcher getWorkflowHandlerSchemaFetcherByWorkflowName(
        String workflowName
    ) {
        return workflowHandlerSchemaFetcherByName
            .get()
            .get(workflowName);
    }

    public List<WorkflowHandlerSchemaFetcher> getWorkflowHandlerSchemaFetchers() {
        return new ArrayList<>(
            workflowHandlerSchemaFetcherByName.get().values()
        );
    }

    public Map<String, WorkflowHandlerSchemaFetcher> getWorkflowHandlerSchemaFetcherMap() {
        return new HashMap<>(workflowHandlerSchemaFetcherByName.get());
    }
}
