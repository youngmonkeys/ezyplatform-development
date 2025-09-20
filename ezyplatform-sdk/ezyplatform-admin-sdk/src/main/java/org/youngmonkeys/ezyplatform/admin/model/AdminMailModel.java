/*
 * Copyright 2022 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.admin.model;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import lombok.ToString;
import org.youngmonkeys.ezyplatform.model.MailAddressModel;

import java.util.*;

@Getter
@ToString
public class AdminMailModel {
    private final String templateName;
    private final MailAddressModel from;
    private final List<String> to;
    private final String title;
    private final String content;
    private final Map<String, Object> parameters;
    private final Locale locale;

    protected AdminMailModel(Builder builder) {
        this.templateName = builder.templateName;
        this.from = builder.from;
        this.to = builder.to;
        this.title = builder.title;
        this.content = builder.content;
        this.parameters = builder.parameters;
        this.locale = builder.locale;
    }

    public String joinTo() {
        return String.join(", ", to);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<AdminMailModel> {
        private String templateName;
        private MailAddressModel from;
        private final List<String> to;
        private String title;
        private String content;
        private Map<String, Object> parameters;
        private Locale locale;

        public Builder() {
            this.to = new ArrayList<>();
        }

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder from(MailAddressModel from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to.add(to);
            return this;
        }

        public Builder to(Collection<String> to) {
            this.to.addAll(to);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder parameter(String name, Object value) {
            if (this.parameters == null) {
                this.parameters = new HashMap<>();
            }
            this.parameters.put(name, value);
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            if (this.parameters == null) {
                this.parameters = new HashMap<>();
            }
            this.parameters.putAll(parameters);
            return this;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        @Override
        public AdminMailModel build() {
            return new AdminMailModel(this);
        }
    }
}
