package lugus.infrastructure.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.user.Usuario;
import lugus.repository.user.UsuarioRepository;

public interface JpaUsuarioRepository extends UsuarioRepository, JpaRepository<Usuario, String> {

}