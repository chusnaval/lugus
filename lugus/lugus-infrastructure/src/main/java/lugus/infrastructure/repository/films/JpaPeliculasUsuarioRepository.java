package lugus.infrastructure.repository.films;



import org.springframework.data.jpa.repository.JpaRepository;



public interface JpaPeliculasUsuarioRepository
        extends lugus.repository.films.PeliculasUsuarioRepository, JpaRepository<lugus.model.films.PeliculasUsuario, Long> {

}
