package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import lugus.model.films.Film;
import lugus.repository.films.PeliculaRepository;


public interface JpaPeliculaRepository extends PeliculaRepository, JpaRepository<Film, Integer>, JpaSpecificationExecutor<Film> {


}