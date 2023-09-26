package org.youngmonkeys.ezyplatform.service;

import java.util.List;

public interface AdminRoleService {

    List<Long> getAdminIdsByRoleName(String roleName);
}
