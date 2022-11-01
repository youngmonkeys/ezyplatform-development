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

package org.youngmonkeys.ezyplatform.service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public interface DataActionCountService {

    BigInteger increaseDataActionCount(
        String dataType,
        String actionType,
        long dataId,
        BigInteger value
    );

    BigInteger getDataActionCount(
        String dataType,
        String actionType,
        long dataId
    );

    Map<Long, BigInteger> getDataActionCountMap(
        String dataType,
        String actionType,
        Collection<Long> dataIds
    );
}
