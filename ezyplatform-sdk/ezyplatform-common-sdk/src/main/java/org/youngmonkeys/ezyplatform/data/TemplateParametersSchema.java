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

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class TemplateParametersSchema {
    private final List<VariableSchema> variables;
    private final String description;
    private final Map<String, Object> properties;

    public TemplateParametersSchema(Builder builder) {
        this.variables = builder.variables;
        this.description = builder.description;
        this.properties = builder.properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
        implements EzyBuilder<TemplateParametersSchema> {
        private List<VariableSchema> variables;
        private String description;
        private Map<String, Object> properties;

        public Builder variables(List<VariableSchema> variables) {
            this.variables = variables;
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
        public TemplateParametersSchema build() {
            return new TemplateParametersSchema(this);
        }
    }

    public static class VariableSchema extends CommonDataSchema {

        public VariableSchema(Builder builder) {
            super(builder);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder
            extends CommonDataSchema.Builder<Builder> {

            @Override
            public VariableSchema build() {
                return new VariableSchema(this);
            }
        }
    }
}
