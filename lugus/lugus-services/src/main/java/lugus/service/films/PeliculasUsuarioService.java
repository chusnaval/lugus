package lugus.service.films;


import lugus.model.user.User;
import lugus.repository.films.FilmUserRepository;
import lugus.service.user.UsuarioService;
import lugus.model.films.Film;
import lugus.model.films.FilmUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculasUsuarioService {
    private final FilmUserRepository repository;
    private final UsuarioService userService;
    private final PeliculaService peliculaService;
    
    public void toggleFavorita(String login, Integer peliculaId, boolean favorita) {
        User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
        
        FilmUser up = repository.findByUserAndFilm(user, pelicula).orElseThrow(() -> new RuntimeException("Relación usuario-película no encontrada"));
                

        up.setFavorite(favorita);
        repository.save(up);
    }
    
    public void toggleOwned(String login, Integer peliculaId) {
    	 User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    	  Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	    Optional<FilmUser> existing =
	    		repository.findByUserAndFilm(user, pelicula);

	    if (existing.isPresent()) {
	    	repository.delete(existing.get());
	    } else {
	        Film p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	        FilmUser up = new FilmUser();
	        up.setUser(user);
	        up.setFilm(p);
	        repository.save(up);
	    }
	}
    
    public void toggleFav(String login, Integer peliculaId) {
   	 User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
   	  Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	    Optional<FilmUser> existing =
	    		repository.findByUserAndFilm(user, pelicula);

	    if (existing.isPresent()) {
	    	existing.get().setFavorite(!existing.get().isFavorite());
	    	repository.save(existing.get());
	    } else {
	        Film p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException("Película no encontrada"));
	        FilmUser up = new FilmUser();
	        up.setFavorite(true);
	        up.setUser(user);
	        up.setFilm(p);
	        repository.save(up);
	    }
	}
   
    
    public List<FilmUser> findByUser(User usuario) {
        return repository.findByUser(usuario);
    }

    public List<FilmUser> findByFilm(Film pelicula) {
        return repository.findByFilm(pelicula);
    }

    public Optional<FilmUser> findByUsuarioAndPelicula(User usuario, Film pelicula) {
        return repository.findByUserAndFilm(usuario, pelicula);
    }

    public FilmUser save(FilmUser entity) {
        return repository.save(entity);
    }

    public void delete(FilmUser entity) {
        repository.delete(entity);
    }
}
