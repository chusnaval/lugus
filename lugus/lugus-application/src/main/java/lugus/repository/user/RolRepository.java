package lugus.repository.user;


import java.util.Optional;

import lugus.model.user.Rol;



public interface RolRepository {

	Optional<Rol> findById(Long id);

	Rol save(Rol rol);

	void deleteById(Long id);

	Optional<Rol> findByLogin(String login);


}
