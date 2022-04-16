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

package org.youngmonkeys.ezyplatform.concurrent;

import com.tvd12.ezyfox.concurrent.EzyThreadFactory;

public class DefaultThreadFactory extends EzyThreadFactory {

    protected DefaultThreadFactory(Builder builder) {
        super(builder);
    }

    public static DefaultThreadFactory create(String poolName) {
        return (DefaultThreadFactory) builder()
            .poolName(poolName)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends EzyThreadFactory.Builder {

        protected Builder() {
            super();
            this.prefix = "ezyplatform";
        }

        @Override
        public DefaultThreadFactory build() {
            return new DefaultThreadFactory(this);
        }

    }

}
