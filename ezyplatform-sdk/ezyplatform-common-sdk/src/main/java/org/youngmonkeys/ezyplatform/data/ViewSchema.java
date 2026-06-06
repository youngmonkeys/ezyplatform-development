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
public class ViewSchema {
    private final String template;
    private final String pageFragmentPageName;
    private final List<String> pageFragmentNames;
    private final List<DataSchema> variables;
    private final String description;
    private final Map<String, Object> properties;

    public ViewSchema(Builder builder) {
        this.template = builder.template;
        this.pageFragmentPageName = builder.pageFragmentPageName;
        this.pageFragmentNames = builder.pageFragmentNames;
        this.variables = builder.variables;
        this.description = builder.description;
        this.properties = builder.properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<ViewSchema> {
        private String template;
        private String pageFragmentPageName;
        private List<String> pageFragmentNames;
        private List<DataSchema> variables;
        private String description;
        private Map<String, Object> properties;

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder pageFragmentPageName(String pageFragmentPageName) {
            this.pageFragmentPageName = pageFragmentPageName;
            return this;
        }

        public Builder pageFragmentNames(List<String> pageFragmentNames) {
            this.pageFragmentNames = pageFragmentNames;
            return this;
        }

        public Builder variables(List<DataSchema> variables) {
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
        public ViewSchema build() {
            return new ViewSchema(this);
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
