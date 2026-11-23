package lugus.repository.imdb;

import java.util.Optional;

import lugus.model.imdb.ImdbTitleBasics;

public interface ImdbTitleBasicsRepository {

	Optional<ImdbTitleBasics> findById(final String id);

	ImdbTitleBasics save(final ImdbTitleBasics imdbTitleBasics);

	void deleteById(final String id);

	Iterable<ImdbTitleBasics> searchByPrimarytitleContainingIgnoreCase(final String query);

	Optional<ImdbTitleBasics> findByTconst(final String tconst);

}
