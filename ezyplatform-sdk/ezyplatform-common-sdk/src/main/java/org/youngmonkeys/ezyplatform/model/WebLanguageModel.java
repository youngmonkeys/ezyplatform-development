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

package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebLanguageModel implements Comparable<WebLanguageModel> {
    protected String code;
    protected String name;
    protected String flag;
    protected int displayOrder;
    protected boolean active;

    @Override
    public int compareTo(WebLanguageModel o) {
        int result = Integer.compare(displayOrder, o.displayOrder);
        if (result == 0) {
            result = code.compareTo(o.code);
        }
        return result;
    }

    public static class Mutable extends WebLanguageModel {

        public Mutable() {
            super(null, null, null, 0, false);
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public void setDisplayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
