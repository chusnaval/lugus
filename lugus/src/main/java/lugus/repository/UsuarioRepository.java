package lugus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

}
