package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbDirector;

public interface ImdbDirectorRepository {

	List<ImdbDirector> findAll();

	Optional<ImdbDirector> findById(final String id);

	ImdbDirector save(final ImdbDirector imdbDirector);

	void deleteById(final String id);

	List<ImdbDirector> findByPrimaryName(final String name);

}
