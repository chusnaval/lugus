package lugus.service.films;


import lugus.model.user.User;
import lugus.repository.films.FilmUserRepository;
import lugus.service.user.UserService;
import lugus.model.films.Film;
import lugus.model.films.FilmUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmUserService {
    private static final String FILM_NOT_FOUND = "Película no encontrada";
	private static final String USER_NOT_FOUND = "Usuario no encontrado";
	private final FilmUserRepository repository;
    private final UserService userService;
    private final FilmService peliculaService;
    
    public void toggleFavorite(String login, Integer peliculaId, boolean favorita) {
        User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException(FILM_NOT_FOUND));
        
        FilmUser up = repository.findByUserAndFilm(user, pelicula).orElseThrow(() -> new RuntimeException("Relación usuario-película no encontrada"));
                

        up.setFavorite(favorita);
        repository.save(up);
    }
    
    public void toggleOwned(String login, Integer peliculaId) {
    	 User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    	  Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException(FILM_NOT_FOUND));
	    Optional<FilmUser> existing =
	    		repository.findByUserAndFilm(user, pelicula);

	    if (existing.isPresent()) {
	    	repository.delete(existing.get());
	    } else {
	        Film p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException(FILM_NOT_FOUND));
	        FilmUser up = new FilmUser();
	        up.setUser(user);
	        up.setFilm(p);
	        repository.save(up);
	    }
	}
    
    public void toggleFav(String login, Integer peliculaId) {
   	 User user = userService.findByLogin(login).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
   	  Film pelicula = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException(FILM_NOT_FOUND));
	    Optional<FilmUser> existing =
	    		repository.findByUserAndFilm(user, pelicula);

	    if (existing.isPresent()) {
	    	existing.get().setFavorite(!existing.get().isFavorite());
	    	repository.save(existing.get());
	    } else {
	        Film p = peliculaService.findById(peliculaId).orElseThrow(() -> new RuntimeException(FILM_NOT_FOUND));
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
