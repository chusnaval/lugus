package lugus.repository.user;


import java.util.Optional;

import lugus.model.user.Role;



public interface RoleRepository {

	Optional<Role> findById(final Long id);

	Role save(final Role role);

	void deleteById(final Long id);

	Optional<Role> findByLogin(final String login);


}
