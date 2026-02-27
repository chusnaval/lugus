package lugus.service.user;

import lugus.model.user.FavoritosUsuario;
import lugus.model.user.Usuario;
import lugus.model.films.Pelicula;
import lugus.model.series.Serie;
import lugus.repository.user.FavoritosUsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritosUsuarioService {
    private final FavoritosUsuarioRepository repository;

    public List<FavoritosUsuario> findByUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<FavoritosUsuario> findByPelicula(Pelicula pelicula) {
        return repository.findByPelicula(pelicula);
    }

    public List<FavoritosUsuario> findBySerie(Serie serie) {
        return repository.findBySerie(serie);
    }

    public FavoritosUsuario findByUsuarioAndPelicula(Usuario usuario, Pelicula pelicula) {
        return repository.findByUsuarioAndPelicula(usuario, pelicula);
    }

    public FavoritosUsuario findByUsuarioAndSerie(Usuario usuario, Serie serie) {
        return repository.findByUsuarioAndSerie(usuario, serie);
    }

    public FavoritosUsuario save(FavoritosUsuario entity) {
        return repository.save(entity);
    }

    public void delete(FavoritosUsuario entity) {
        repository.delete(entity);
    }
}
