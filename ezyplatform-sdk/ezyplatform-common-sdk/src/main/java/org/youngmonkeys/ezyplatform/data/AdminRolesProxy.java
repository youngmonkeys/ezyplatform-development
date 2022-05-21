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

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyfox.io.EzyCollections.containsAny;

@Builder
public class AdminRolesProxy {
    @Getter
    private boolean supperAdmin;
    @Getter
    private boolean specialAdmin;
    private Set<String> accessibleUris;
    private FeatureURIManager featureUriManager;

    public boolean isAccessible(String url) {
        if (specialAdmin) {
            return true;
        }
        String feature = featureUriManager.getFeatureByURI(
            HttpMethod.GET,
            url
        );
        if (feature == null) {
            return true;
        }
        return accessibleUris.contains(url);
    }

    public boolean isAccessibleAny(List<String> urls) {
        for (String url : urls) {
            if (isAccessible(url)) {
                return true;
            }
        }
        return false;
    }

    public static AdminRolesProxy create(
        Set<Long> roleIds,
        long superAdminRoleId,
        Set<Long> specialRoleIds,
        FeatureURIManager featureUriManager,
        Map<Long, Map<String, Map<String, Set<HttpMethod>>>> methodsUriMapByFeatureByRoleId
    ) {
        Set<String> accessibleUris = new HashSet<>();
        for (Long roleId : roleIds) {
            Map<String, Map<String, Set<HttpMethod>>> methodsUriMapByFeature =
                methodsUriMapByFeatureByRoleId.get(roleId);
            if (methodsUriMapByFeature != null) {
                for (Map<String, Set<HttpMethod>> methodsUriMap : methodsUriMapByFeature.values()) {
                    for (Map.Entry<String, Set<HttpMethod>> entry : methodsUriMap.entrySet()) {
                        if (entry.getValue().contains(HttpMethod.GET)) {
                            accessibleUris.add(entry.getKey());
                        }
                    }
                }
            }
        }
        return AdminRolesProxy.builder()
            .accessibleUris(accessibleUris)
            .featureUriManager(featureUriManager)
            .specialAdmin(containsAny(roleIds, specialRoleIds))
            .supperAdmin(roleIds.contains(superAdminRoleId))
            .build();
    }
}
