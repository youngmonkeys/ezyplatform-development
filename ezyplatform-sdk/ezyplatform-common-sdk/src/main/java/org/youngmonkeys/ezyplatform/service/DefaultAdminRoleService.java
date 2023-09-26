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
