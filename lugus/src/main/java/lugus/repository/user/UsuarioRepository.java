package lugus.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.user.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, String> {

}
