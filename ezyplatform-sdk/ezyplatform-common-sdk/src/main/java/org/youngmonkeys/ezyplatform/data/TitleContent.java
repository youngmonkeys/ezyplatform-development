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

package org.youngmonkeys.ezyplatform.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@Getter
@AllArgsConstructor
public class TitleContent {
    private final String title;
    private final String content;

    public static TitleContent fromTemplates(
        String titleTemplate,
        String contentTemplate,
        Map<String, Object> parameters
    ) {
        String title = titleTemplate;
        String content = contentTemplate;
        for (String name : parameters.keySet()) {
            Object value = parameters.get(name);
            String valueText = value != null ? value.toString() : EMPTY_STRING;
            String variableName = "${" + name + "}";
            title = title.replace(variableName, valueText);
            content = content.replace(variableName, valueText);
        }
        return new TitleContent(title, content);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<TitleContent> {
        private String title;
        private String content;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        @Override
        public TitleContent build() {
            return new TitleContent(title, content);
        }
    }
}
