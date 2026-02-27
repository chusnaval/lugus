package lugus.repository.imdb;

import java.util.Optional;

import lugus.model.imdb.ImdbTitleAkas;

public interface ImdbTitleAkasRepository {

	Optional<ImdbTitleAkas> findById(String id);

	ImdbTitleAkas save(ImdbTitleAkas imdbTitleAkas);

	void deleteById(String id);

	Optional<ImdbTitleAkas> findByTitleId(String titleId);

}
