package lugus.infrastructure.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.user.Role;
import lugus.repository.user.RoleRepository;


public interface JpaRoleRepository extends RoleRepository, JpaRepository<Role, Long> {

}