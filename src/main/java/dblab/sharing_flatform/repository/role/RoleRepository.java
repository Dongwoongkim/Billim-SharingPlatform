package dblab.sharing_flatform.repository.role;

import dblab.sharing_flatform.domain.role.Role;
import dblab.sharing_flatform.domain.role.RoleType;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByRoleType(RoleType roleType);
}
