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

package org.youngmonkeys.ezyplatform.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class WorkflowHandlerSchema {
    private final DataSchema argumentSchema;
    private final DataSchema resultSchema;
    private final String description;
    private final Map<String, Object> properties;

    public WorkflowHandlerSchema(Builder builder) {
        this.argumentSchema = builder.argumentSchema;
        this.resultSchema = builder.resultSchema;
        this.description = builder.description;
        this.properties = builder.properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<WorkflowHandlerSchema> {
        private DataSchema argumentSchema;
        private DataSchema resultSchema;
        private String description;
        private Map<String, Object> properties;

        public Builder argumentSchema(DataSchema argumentSchema) {
            this.argumentSchema = argumentSchema;
            return this;
        }

        public Builder resultSchema(DataSchema resultSchema) {
            this.resultSchema = resultSchema;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public WorkflowHandlerSchema build() {
            return new WorkflowHandlerSchema(this);
        }
    }

    public static class DataSchema extends CommonDataSchema {

        public DataSchema(Builder builder) {
            super(builder);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder
            extends CommonDataSchema.Builder<Builder> {

            @Override
            public DataSchema build() {
                return new DataSchema(this);
            }
        }
    }
}
