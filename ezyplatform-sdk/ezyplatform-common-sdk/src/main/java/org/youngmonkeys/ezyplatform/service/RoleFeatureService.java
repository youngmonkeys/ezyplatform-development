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

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import org.youngmonkeys.ezyplatform.entity.TargetType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleFeatureService {

    Map<String, Map<String, List<HttpMethod>>>
        getMethodsUriMapOfFeatureByRoleId(
            long roleId
        );

    default Map<String, Map<String, List<HttpMethod>>>
        getMethodsUriMapOfFeatureByRoleId(
            TargetType targetType,
            long roleId
    ) {
        return getMethodsUriMapOfFeatureByRoleId(
            targetType.toString(),
            roleId
        );
    }

    Map<String, Map<String, List<HttpMethod>>>
        getMethodsUriMapOfFeatureByRoleId(
            String targetType,
            long roleId
        );

    Map<Long, Map<String, Map<String, Set<HttpMethod>>>>
        getMethodsUriMapByFeatureByRoleId();
}
