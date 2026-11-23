package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbDirectorFilm;

public interface ImdbDirectorFilmRepository {

	List<ImdbDirectorFilm> findAll();

	Optional<ImdbDirectorFilm> findById(final String id);

	ImdbDirectorFilm save(final ImdbDirectorFilm imdbDirectorFilm);

	void deleteById(final String id);

	List<ImdbDirectorFilm> findByTitleContainingIgnoreCase(final String title);
	
}
