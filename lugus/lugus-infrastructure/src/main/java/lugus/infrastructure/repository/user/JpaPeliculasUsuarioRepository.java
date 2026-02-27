package lugus.infrastructure.repository.user;

import lugus.model.user.PeliculasUsuario;

import lugus.repository.user.PeliculasUsuarioRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPeliculasUsuarioRepository
        extends PeliculasUsuarioRepository, JpaRepository<PeliculasUsuario, Long> {

}
