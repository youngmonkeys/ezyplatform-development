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
import org.youngmonkeys.ezyplatform.entity.RoleFeature;
import org.youngmonkeys.ezyplatform.entity.TargetType;
import org.youngmonkeys.ezyplatform.repo.RoleFeatureRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.settingNameTargetRoleFeatures;

public class DefaultRoleFeatureService implements RoleFeatureService {

    protected final String targetType;
    private final RoleFeatureRepository roleFeatureRepository;
    private final AtomicReference<Map<Long, Map<String, Map<String, Set<HttpMethod>>>>>
        methodsUriMapByFeatureByRoleIdRef = new AtomicReference<>();

    public DefaultRoleFeatureService(
        TargetType targetType,
        SettingService settingService,
        RoleFeatureRepository roleFeatureRepository
    ) {
        this(
            targetType.toString(),
            settingService,
            roleFeatureRepository
        );
    }

    public DefaultRoleFeatureService(
        String targetType,
        SettingService settingService,
        RoleFeatureRepository roleFeatureRepository
    ) {
        this.targetType = targetType;
        this.roleFeatureRepository = roleFeatureRepository;
        settingService.watchLastUpdatedTime(
            settingNameTargetRoleFeatures(targetType),
            1,
            this::cacheRoleFeatures
        );
        this.cacheRoleFeaturesIfNull();
    }

    @Override
    public Map<String, Map<String, List<HttpMethod>>> getMethodsUriMapOfFeatureByRoleId(
        long roleId
    ) {
        return getMethodsUriMapOfFeatureByRoleId(targetType, roleId);
    }

    @Override
    public Map<String, Map<String, List<HttpMethod>>> getMethodsUriMapOfFeatureByRoleId(
        TargetType targetType,
        long roleId
    ) {
        return getMethodsUriMapOfFeatureByRoleId(
            targetType.toString(),
            roleId
        );
    }

    @Override
    public Map<String, Map<String, List<HttpMethod>>> getMethodsUriMapOfFeatureByRoleId(
        String targetType,
        long roleId
    ) {
        List<RoleFeature> entities = roleFeatureRepository.findByRoleIdAndTarget(
            roleId,
            targetType
        );
        Map<String, Map<String, List<HttpMethod>>> methodsUriMapOfFeature =
            new HashMap<>();
        for (RoleFeature entity : entities) {
            methodsUriMapOfFeature.computeIfAbsent(
                entity.getFeature(),
                k -> new HashMap<>()
            ).computeIfAbsent(
                entity.getFeatureUri(),
                k -> new ArrayList<>()
            ).add(HttpMethod.valueOf(entity.getFeatureMethod()));
        }
        return methodsUriMapOfFeature;
    }

    @Override
    public Map<Long, Map<String, Map<String, Set<HttpMethod>>>>
        getMethodsUriMapByFeatureByRoleId() {
        return methodsUriMapByFeatureByRoleIdRef.get();
    }

    protected void cacheRoleFeatures() {
        Map<Long, Map<String, Map<String, Set<HttpMethod>>>> methodsUriMapByFeatureByRoleId
            = new HashMap<>();
        roleFeatureRepository.findListByField(
            "target",
            targetType
        ).forEach(entity ->
            methodsUriMapByFeatureByRoleId.computeIfAbsent(entity.getRoleId(), it ->
                new HashMap<>()
            ).computeIfAbsent(entity.getFeature(), it ->
                new HashMap<>()
            ).computeIfAbsent(entity.getFeatureUri(), it ->
                new HashSet<>()
            ).add(HttpMethod.valueOf(entity.getFeatureMethod()))
        );
        methodsUriMapByFeatureByRoleIdRef.set(methodsUriMapByFeatureByRoleId);
    }

    protected void cacheRoleFeaturesIfNull() {
        if (methodsUriMapByFeatureByRoleIdRef.get() == null) {
            cacheRoleFeatures();
        }
    }
}
