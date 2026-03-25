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

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Workflow {

    @Getter
    private final String name;
    @Getter
    private final String displayName;
    private List<WorkflowHandler> handlers;

    public Workflow(Builder builder) {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.handlers = Collections.emptyList();
    }

    public void run(
        Map<String, Object> input,
        Map<String, Object> output
    ) {
        for (WorkflowHandler handler : handlers) {
            handler.handle(input, output);
        }
    }

    public void saveHandlers(
        List<WorkflowHandler> handlers
    ) {
        this.handlers = handlers;
    }

    public List<WorkflowHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    public static class Builder implements EzyBuilder<Workflow> {
        private String name;
        private String displayName;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public Workflow build() {
            return new Workflow(this);
        }
    }
}
