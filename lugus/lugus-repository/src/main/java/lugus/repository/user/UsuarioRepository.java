package lugus.repository.user;

import java.util.Optional;

import lugus.model.user.User;


public interface UsuarioRepository {

	Optional<User> findById(String id);

	User save(User usuario);

	void deleteById(String id);

}
