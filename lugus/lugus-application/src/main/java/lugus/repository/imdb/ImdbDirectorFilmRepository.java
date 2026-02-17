package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbDirectorFilm;

public interface ImdbDirectorFilmRepository {

	List<ImdbDirectorFilm> findAll();

	Optional<ImdbDirectorFilm> findById(String id);

	ImdbDirectorFilm save(ImdbDirectorFilm imdbDirectorFilm);

	void deleteById(String id);

	List<ImdbDirectorFilm> findByTitleContainingIgnoreCase(final String title);
	
}
