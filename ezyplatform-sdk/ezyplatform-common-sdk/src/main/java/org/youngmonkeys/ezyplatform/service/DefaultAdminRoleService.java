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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.repo.AdminIdRoleRepository;
import org.youngmonkeys.ezyplatform.result.IdResult;

import java.util.List;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultAdminRoleService implements AdminRoleService {

    private final AdminIdRoleRepository adminIdRoleRepository;

    @Override
    public List<Long> getAdminIdsByRoleName(String roleName) {
        return newArrayList(
            adminIdRoleRepository.findAdminIdsByRoleName(roleName),
            IdResult::getId
        );
    }
}
