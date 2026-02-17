package lugus.infrastructure.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.user.Rol;
import lugus.repository.user.RolRepository;

public interface JpaRolRepository extends RolRepository, JpaRepository<Rol, Long> {

}