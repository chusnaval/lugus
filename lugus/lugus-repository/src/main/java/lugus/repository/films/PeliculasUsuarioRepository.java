
package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import lugus.model.films.Pelicula;
import lugus.model.films.PeliculasUsuario;
import lugus.model.user.Usuario;

public interface PeliculasUsuarioRepository {
    List<PeliculasUsuario> findByUsuario(Usuario usuario);

    List<PeliculasUsuario> findByPelicula(Pelicula pelicula);

    Optional<PeliculasUsuario> findByUsuarioAndPelicula(Usuario usuario, Pelicula pelicula);

    void delete(PeliculasUsuario entity);

    PeliculasUsuario save(PeliculasUsuario entity);
}
