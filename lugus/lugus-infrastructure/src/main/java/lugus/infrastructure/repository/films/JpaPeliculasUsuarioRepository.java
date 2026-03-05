package lugus.infrastructure.repository.films;



import lugus.repository.user.PeliculasUsuarioRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPeliculasUsuarioRepository
        extends PeliculasUsuarioRepository, JpaRepository<lugus.model.films.PeliculasUsuario, Long> {

}
