package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.films.PeliculasOtros;
import lugus.repository.PeliculasOtrosRepository;

@Service
@RequiredArgsConstructor
public class PeliculasOtrosService {

	private final PeliculasOtrosRepository peliculasOtrosRepository;

	public List<PeliculasOtros> findByImdbId(final String tconst) {
		return peliculasOtrosRepository.findByImdbId(tconst);
	}

	/**
	 * If exist, obtain the first idFilm equivalent to the tconst
	 * 
	 * @param tconst
	 * @return
	 */
	public Integer getIdFilm(String tconst) {
		Integer idFilm = null;
		List<PeliculasOtros> po = findByImdbId(tconst);
		if (!po.isEmpty()) {
			idFilm = po.get(0).getPelicula().getId();
		}

		return idFilm;
	}
	
	/**
	 * Is film registered as Pelicula register
	 * @param tconst
	 * @return
	 */
	public boolean isFilmRegistered(String tconst) {
		List<PeliculasOtros> po = this.findByImdbId(tconst);
		return !po.isEmpty();
	}
}
