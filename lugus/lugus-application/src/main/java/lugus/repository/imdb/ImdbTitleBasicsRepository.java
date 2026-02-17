package lugus.repository.imdb;

import java.util.Optional;

import lugus.model.imdb.ImdbTitleBasics;

public interface ImdbTitleBasicsRepository {

	Optional<ImdbTitleBasics> findById(String id);

	ImdbTitleBasics save(ImdbTitleBasics imdbTitleBasics);

	void deleteById(String id);

}
