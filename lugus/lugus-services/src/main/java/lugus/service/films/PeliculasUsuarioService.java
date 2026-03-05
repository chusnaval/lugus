package lugus.service.films;


import lugus.model.user.Usuario;
import lugus.repository.films.PeliculasUsuarioRepository;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculasUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeliculasUsuarioService {
    private final PeliculasUsuarioRepository repository;

    public List<PeliculasUsuario> findByUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<PeliculasUsuario> findByPelicula(Pelicula pelicula) {
        return repository.findByPelicula(pelicula);
    }

    public PeliculasUsuario findByUsuarioAndPelicula(Usuario usuario, Pelicula pelicula) {
        return repository.findByUsuarioAndPelicula(usuario, pelicula);
    }

    public PeliculasUsuario save(PeliculasUsuario entity) {
        return repository.save(entity);
    }

    public void delete(PeliculasUsuario entity) {
        repository.delete(entity);
    }
}
