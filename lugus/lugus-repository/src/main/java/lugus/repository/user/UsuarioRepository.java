package lugus.repository.user;

import java.util.Optional;

import lugus.model.user.Usuario;


public interface UsuarioRepository {

	Optional<Usuario> findById(String id);

	Usuario save(Usuario usuario);

	void deleteById(String id);

}
