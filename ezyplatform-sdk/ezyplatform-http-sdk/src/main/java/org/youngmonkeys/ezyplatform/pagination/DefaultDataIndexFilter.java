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

package org.youngmonkeys.ezyplatform.pagination;

import com.tvd12.ezydata.database.query.EzyQueryConditionBuilder;
import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class DefaultDataIndexFilter implements DataIndexFilter {
    public final String dataType;
    public final Collection<String> keywords;

    protected DefaultDataIndexFilter(Builder<?> builder) {
        this.dataType = builder.dataType;
        this.keywords = builder.keywords;
    }

    @Override
    public List<String> selectionFields() {
        return Collections.singletonList("e.dataId");
    }

    @Override
    public String countField() {
        return "e.dataId";
    }

    @Override
    public String matchingCondition() {
        EzyQueryConditionBuilder answer = new EzyQueryConditionBuilder();
        if (dataType != null) {
            answer.and("e.dataType = :dataType");
        }
        if (keywords != null) {
            answer.and("e.keyword in :keywords");
        }
        return answer.build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>>
        implements EzyBuilder<DefaultDataIndexFilter> {

        private String dataType;
        private Collection<String> keywords;

        public T dataType(String dataType) {
            this.dataType = dataType;
            return (T) this;
        }

        public T keywords(Collection<String> keywords) {
            this.keywords = keywords;
            return (T) this;
        }

        @Override
        public DefaultDataIndexFilter build() {
            return new DefaultDataIndexFilter(this);
        }
    }
}
