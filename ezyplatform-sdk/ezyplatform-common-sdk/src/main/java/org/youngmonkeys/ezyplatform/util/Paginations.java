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

package org.youngmonkeys.ezyplatform.util;

import java.util.Collections;
import java.util.List;

public final class Paginations {
    private Paginations() {}

    public static int getNumberOfPages(int total, int pageSize) {
        if (total % pageSize == 0) {
            return total / pageSize;
        } else {
            return total / pageSize + 1;
        }
    }

    public static <T> List<T> getPageItems(
        List<T> items,
        int pageNumber,
        int pageSize
    ) {
        int from = pageSize * (pageNumber - 1);
        if (from >= items.size()) {
            return Collections.emptyList();
        }
        int to = pageSize * pageNumber;
        if (from == to) {
            return Collections.emptyList();
        }
        if (to > items.size()) {
            to = items.size();
        }
        return items.subList(from, to);
    }
}
