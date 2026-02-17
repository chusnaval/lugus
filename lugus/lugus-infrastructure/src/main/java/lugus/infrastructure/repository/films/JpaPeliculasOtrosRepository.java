package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.films.PeliculasOtros;
import lugus.repository.films.PeliculasOtrosRepository;

public interface JpaPeliculasOtrosRepository extends PeliculasOtrosRepository, JpaRepository<PeliculasOtros, Integer> {

}