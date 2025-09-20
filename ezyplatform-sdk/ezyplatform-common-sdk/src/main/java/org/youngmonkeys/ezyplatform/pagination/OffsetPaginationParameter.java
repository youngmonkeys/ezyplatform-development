/*
 * Copyright 2023 youngmonkeys.org
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tvd12.ezyfox.reflect.EzyClasses;
import lombok.Getter;
import lombok.Setter;
import org.youngmonkeys.ezyplatform.data.PaginationParameter;

import java.util.Collections;
import java.util.Map;

@Getter
public abstract class OffsetPaginationParameter
    implements PaginationParameter {

    @Setter
    private transient Integer offset;

    @JsonIgnore
    private final transient String orderBy;

    public OffsetPaginationParameter(String orderBy) {
        this.orderBy = orderBy;
    }

    public OffsetPaginationParameter(
        Integer offset,
        String orderBy
    ) {
        this.offset = offset;
        this.orderBy = orderBy;
    }

    public OffsetPaginationParameter nextOffset(int limit) {
        OffsetPaginationParameter instance = newInstance();
        instance.setOffset(offset + limit);
        return instance;
    }

    public OffsetPaginationParameter previousOffset(int limit) {
        OffsetPaginationParameter instance = newInstance();
        instance.setOffset(offset - limit);
        return instance;
    }

    @Override
    public boolean isEmpty() {
        return offset == 0;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public abstract String sortOrder();

    public OffsetPaginationParameter newInstance() {
        return EzyClasses.newInstance(getClass());
    }
}
