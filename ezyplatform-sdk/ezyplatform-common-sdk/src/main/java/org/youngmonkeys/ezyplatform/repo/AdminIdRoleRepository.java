package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezyfox.database.annotation.EzyQuery;
import org.youngmonkeys.ezyplatform.result.IdResult;

import java.util.List;

public interface AdminIdRoleRepository {

    @EzyQuery(
        "SELECT e.adminId FROM AdminRole e " +
            "INNER JOIN AdminRoleName a " +
            "ON e.roleId = a.id " +
            "WHERE a.name = ?0"
    )
    List<IdResult> findAdminIdsByRoleName(
        String roleName
    );
}
