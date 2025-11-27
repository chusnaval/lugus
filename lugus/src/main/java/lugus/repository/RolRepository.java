package lugus.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long>{

	Optional<Rol> findByLogin(String login);


}
