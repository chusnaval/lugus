package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.films.PeliculasUsuario;
import lugus.repository.films.PeliculaUsuarioRepository;


public interface JpaPeliculaUsuarioRepository   extends PeliculaUsuarioRepository, JpaRepository<PeliculasUsuario, Integer> {

}
