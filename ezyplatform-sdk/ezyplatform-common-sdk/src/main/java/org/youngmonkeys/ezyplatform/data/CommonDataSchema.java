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
import org.youngmonkeys.ezyplatform.event.EventSchema;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class CommonDataSchema {
    protected final Class<?> dataType;
    protected final Class<?> itemType;
    protected final Class<?> arrayItemType;
    protected final Class<?> keyType;
    protected final Class<?> valueType;
    protected final String name;
    protected final boolean required;
    protected final String description;
    protected final String example;
    protected final List<EventSchema.DataSchema> fields;

    public CommonDataSchema(Builder<?> builder) {
        this.dataType = builder.dataType;
        this.itemType = builder.itemType;
        this.arrayItemType = builder.arrayItemType;
        this.keyType = builder.keyType;
        this.valueType = builder.valueType;
        this.name = builder.name;
        this.required = builder.required;
        this.description = builder.description;
        this.example = builder.example;
        this.fields = builder.fields;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<CommonDataSchema> {
        protected Class<?> dataType;
        protected Class<?> itemType;
        protected Class<?> arrayItemType;
        protected Class<?> keyType;
        protected Class<?> valueType;
        protected String name;
        protected boolean required = Boolean.TRUE;
        protected String description;
        protected String example;
        protected List<EventSchema.DataSchema> fields;

        public T dataType(Class<?> dataType) {
            this.dataType = dataType;
            return (T) this;
        }

        public T itemType(Class<?> itemType) {
            this.itemType = itemType;
            return (T) this;
        }

        public T arrayItemType(Class<?> arrayItemType) {
            this.arrayItemType = arrayItemType;
            return (T) this;
        }

        public T keyType(Class<?> keyType) {
            this.keyType = keyType;
            return (T) this;
        }

        public T valueType(Class<?> valueType) {
            this.valueType = valueType;
            return (T) this;
        }

        public T name(String name) {
            this.name = name;
            return (T) this;
        }

        public T required(boolean required) {
            this.required = required;
            return (T) this;
        }

        public T description(String description) {
            this.description = description;
            return (T) this;
        }

        public T example(String example) {
            this.example = example;
            return (T) this;
        }

        public T field(EventSchema.DataSchema field) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            this.fields.add(field);
            return (T) this;
        }

        public T fields(List<EventSchema.DataSchema> fields) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            this.fields.addAll(fields);
            return (T) this;
        }

        @Override
        public CommonDataSchema build() {
            return new CommonDataSchema(this);
        }
    }
}
