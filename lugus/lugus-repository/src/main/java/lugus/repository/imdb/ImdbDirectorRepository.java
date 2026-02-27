package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbDirector;

public interface ImdbDirectorRepository {

	List<ImdbDirector> findAll();

	Optional<ImdbDirector> findById(String id);

	ImdbDirector save(ImdbDirector imdbDirector);

	void deleteById(String id);

	List<ImdbDirector> findByPrimaryName(String name);

}
