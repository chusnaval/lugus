package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import lugus.model.films.Film;
import lugus.repository.films.FilmRepository;


public interface JpaFilmRepository extends FilmRepository, JpaRepository<Film, Integer>, JpaSpecificationExecutor<Film> {


}