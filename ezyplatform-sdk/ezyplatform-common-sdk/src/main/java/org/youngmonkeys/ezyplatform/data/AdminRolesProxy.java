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

import java.util.*;

import static com.tvd12.ezyfox.io.EzyCollections.containsAny;

@Builder
public class AdminRolesProxy {
    @Getter
    private boolean superAdmin;
    @Getter
    private boolean specialAdmin;
    private FeatureURIManager featureUriManager;
    private Map<String, Set<HttpMethod>> accessibleMethodsByUri;

    public boolean isAccessible(String uri) {
        return isAccessible(uri, HttpMethod.GET);
    }

    public boolean isAccessible(String uri, String method) {
        return isAccessible(uri, HttpMethod.valueOf(method));
    }

    public boolean isAccessible(String uri, HttpMethod method) {
        if (specialAdmin) {
            return true;
        }
        String feature = featureUriManager.getFeatureByURI(
            method,
            uri
        );
        if (feature == null) {
            return true;
        }
        Set<HttpMethod> methods = accessibleMethodsByUri.getOrDefault(
            uri,
            Collections.emptySet()
        );
        return methods.contains(method);
    }

    public boolean isAccessibleAny(List<String> urls) {
        for (String url : urls) {
            if (isAccessible(url)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccessibleAll(List<String> urls) {
        for (String url : urls) {
            if (!isAccessible(url)) {
                return false;
            }
        }
        return true;
    }

    public static AdminRolesProxy create(
        Set<Long> roleIds,
        long superAdminRoleId,
        Set<Long> specialRoleIds,
        FeatureURIManager featureUriManager,
        Map<Long, Map<String, Map<String, Set<HttpMethod>>>> methodsUriMapByFeatureByRoleId
    ) {
        Map<String, Set<HttpMethod>> accessibleMethodsByUri = new HashMap<>();
        for (Long roleId : roleIds) {
            Map<String, Map<String, Set<HttpMethod>>> methodsUriMapByFeature =
                methodsUriMapByFeatureByRoleId.get(roleId);
            if (methodsUriMapByFeature != null) {
                for (Map<String, Set<HttpMethod>> methodsUriMap : methodsUriMapByFeature.values()) {
                    for (Map.Entry<String, Set<HttpMethod>> entry : methodsUriMap.entrySet()) {
                        accessibleMethodsByUri.compute(entry.getKey(), (k, v) -> {
                            if (v == null) {
                                return entry.getValue();
                            }
                            Set<HttpMethod> methods = new HashSet<>(v);
                            methods.addAll(entry.getValue());
                            return methods;
                        });
                    }
                }
            }
        }
        return AdminRolesProxy.builder()
            .featureUriManager(featureUriManager)
            .accessibleMethodsByUri(accessibleMethodsByUri)
            .specialAdmin(containsAny(roleIds, specialRoleIds))
            .superAdmin(roleIds.contains(superAdminRoleId))
            .build();
    }
}
