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
import lombok.ToString;
import org.youngmonkeys.ezyplatform.data.CommonDataSchema;

import java.util.List;

@Getter
@ToString
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

    public static class DataSchema extends CommonDataSchema {

        public DataSchema(DataSchema.Builder builder) {
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
