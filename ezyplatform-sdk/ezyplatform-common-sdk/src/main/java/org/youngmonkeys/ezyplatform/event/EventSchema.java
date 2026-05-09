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

package org.youngmonkeys.ezyplatform.event;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventSchema {
    private final String description;
    private final DataSchema argumentSchema;
    private final DataSchema resultSchema;
    private final List<String> examples;

    public EventSchema(Builder builder) {
        this.description = builder.description;
        this.argumentSchema = builder.argumentSchema;
        this.resultSchema = builder.resultSchema;
        this.examples = builder.examples;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<EventSchema> {
        private String description;
        private DataSchema argumentSchema;
        private DataSchema resultSchema;
        private List<String> examples;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder argumentSchema(DataSchema argumentSchema) {
            this.argumentSchema = argumentSchema;
            return this;
        }

        public Builder resultSchema(DataSchema resultSchema) {
            this.resultSchema = resultSchema;
            return this;
        }

        public Builder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        @Override
        public EventSchema build() {
            return new EventSchema(this);
        }
    }

    @Getter
    public static class DataSchema {
        private final Class<?> dataType;
        private final String name;
        private final boolean required;
        private final String description;
        private final String example;
        private final List<DataSchema> fields;

        public DataSchema(DataSchema.Builder builder) {
            this.dataType = builder.dataType;
            this.name = builder.name;
            this.required = builder.required;
            this.description = builder.description;
            this.example = builder.example;
            this.fields = builder.fields;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder implements EzyBuilder<DataSchema> {
            private Class<?> dataType;
            private String name;
            private boolean required;
            private String description;
            private String example;
            private List<DataSchema> fields;

            public Builder dataType(Class<?> dataType) {
                this.dataType = dataType;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder required(boolean required) {
                this.required = required;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder example(String example) {
                this.example = example;
                return this;
            }

            public Builder field(DataSchema field) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.add(field);
                return this;
            }

            public Builder fields(List<DataSchema> fields) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.addAll(fields);
                return this;
            }

            @Override
            public DataSchema build() {
                return new DataSchema(this);
            }
        }
    }
}
