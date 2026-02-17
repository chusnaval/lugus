package lugus.repository.films;

import java.util.List;

import lugus.model.films.PeliculasOtros;


public interface PeliculasOtrosRepository {

	List<PeliculasOtros> findByImdbId(String tconst);

}
