package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.films.PeliculasUsuario;

public interface JpaPeliculaUsuarioRepository   extends lugus.repository.films.PeliculaUsuarioRepository, JpaRepository<PeliculasUsuario, Integer> {

}
