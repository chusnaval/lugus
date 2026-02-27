package lugus.repository.user;

import java.util.List;



import lugus.model.films.Pelicula;
import lugus.model.series.Serie;
import lugus.model.user.FavoritosUsuario;
import lugus.model.user.Usuario;


public interface FavoritosUsuarioRepository {
    List<FavoritosUsuario> findByUsuario(Usuario usuario);

    List<FavoritosUsuario> findByPelicula(Pelicula pelicula);

    List<FavoritosUsuario> findBySerie(Serie serie);

    FavoritosUsuario findByUsuarioAndPelicula(Usuario usuario, Pelicula pelicula);

    FavoritosUsuario findByUsuarioAndSerie(Usuario usuario, Serie serie);

    FavoritosUsuario save(FavoritosUsuario entity);

    void delete(FavoritosUsuario entity);

}
