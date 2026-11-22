package lugus.repository.user;


import java.util.Optional;

import lugus.model.user.Role;



public interface RolRepository {

	Optional<Role> findById(Long id);

	Role save(Role rol);

	void deleteById(Long id);

	Optional<Role> findByLogin(String login);


}
