package lugus.service.films;


import lugus.model.user.Usuario;
import lugus.repository.films.PeliculasUsuarioRepository;
import lugus.service.user.UsuarioService;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculasUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculasUsuarioService {
    private final PeliculasUsuarioRepository repository;
    private final UsuarioService userService;
    private final PeliculaService peliculaService;
    
    public void toggleFavorita(String login, Integer peliculaId, boolean favorita) {
        Usuario user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Pelicula pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
        
        PeliculasUsuario up = repository.findByUsuarioAndPelicula(user, pelicula).orElseThrow(() -> new RuntimeException("Relación usuario-película no encontrada"));
                

        up.setFavorita(favorita);
        repository.save(up);
    }
    
    public void toggleOwned(String login, Integer peliculaId) {
    	 Usuario user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    	  Pelicula pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	    Optional<PeliculasUsuario> existing =
	    		repository.findByUsuarioAndPelicula(user, pelicula);

	    if (existing.isPresent()) {
	    	repository.delete(existing.get());
	    } else {
	        Pelicula p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	        PeliculasUsuario up = new PeliculasUsuario();
	        up.setUsuario(user);
	        up.setPelicula(p);
	        repository.save(up);
	    }
	}
    
    public void toggleFav(String login, Integer peliculaId) {
   	 Usuario user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
   	  Pelicula pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	    Optional<PeliculasUsuario> existing =
	    		repository.findByUsuarioAndPelicula(user, pelicula);

	    if (existing.isPresent()) {
	    	existing.get().setFavorita(!existing.get().isFavorita());
	    	repository.save(existing.get());
	    } else {
	        Pelicula p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	        PeliculasUsuario up = new PeliculasUsuario();
	        up.setFavorita(true);
	        up.setUsuario(user);
	        up.setPelicula(p);
	        repository.save(up);
	    }
	}
   
    
    public List<PeliculasUsuario> findByUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<PeliculasUsuario> findByPelicula(Pelicula pelicula) {
        return repository.findByPelicula(pelicula);
    }

    public Optional<PeliculasUsuario> findByUsuarioAndPelicula(Usuario usuario, Pelicula pelicula) {
        return repository.findByUsuarioAndPelicula(usuario, pelicula);
    }

    public PeliculasUsuario save(PeliculasUsuario entity) {
        return repository.save(entity);
    }

    public void delete(PeliculasUsuario entity) {
        repository.delete(entity);
    }
}
