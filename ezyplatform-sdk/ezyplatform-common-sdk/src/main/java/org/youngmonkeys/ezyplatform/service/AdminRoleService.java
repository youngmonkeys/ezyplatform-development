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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.model.AdminRoleModel;
import org.youngmonkeys.ezyplatform.model.AdminRoleNameModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AdminRoleService {

    long getRoleIdByName(
        String roleName
    );

    List<Long> getAdminIdsByRoleName(
        String roleName
    );

    boolean containsAdminRole(
        long roleId,
        long adminId
    );

    boolean containsAdminRoleName(
        long roleId
    );

    boolean containsAllAdminRoleIds(
        Collection<Long> roleIds
    );

    List<AdminRoleNameModel> getAdminRoleNames();

    List<AdminRoleNameModel> getAdminRoleNamesByIds(
        Collection<Long> roleIds
    );

    List<AdminRoleNameModel> getAdminRoleNamesByAdminId(
        long adminId
    );

    List<AdminRoleNameModel> getAdminRoleNamesByPriorityGte(
        int priorityGte
    );

    List<AdminRoleNameModel> getVisibleAdminRoleNamesByAdminId(
        long adminId
    );

    AdminRoleNameModel getAdminRoleNameById(
        long id
    );

    AdminRoleNameModel getAdminRoleNameByName(
        String name
    );

    AdminRoleNameModel getAdminRoleNameByNameOrDisplayName(
        String name,
        String displayName
    );

    Set<Long> getRoleIdsByAdminId(
        long adminId
    );

    List<AdminRoleModel> getAdminRolesByRoleId(
        long roleId
    );

    int getMinAdminRolePriority(
        long adminId
    );

    int getMinPriorityByRoleIds(
        Collection<Long> roleIds
    );
}
