/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.fetcher;

import org.youngmonkeys.ezyplatform.model.CommonEntityModel;

import java.util.Collection;
import java.util.Map;

public interface CommonEntityFetcher {

    CommonEntityModel getEntityById(long entityId);

    Map<Long, CommonEntityModel> getEntityMapByIds(
        Collection<Long> entityIds
    );

    String getEntityType();

    default String getModelName() {
        return getEntityType();
    }

    default int getPriority() {
        return 1;
    }
}
