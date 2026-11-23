package lugus.repository.imdb;

import java.util.Optional;

import lugus.model.imdb.ImdbTitleAkas;

public interface ImdbTitleAkasRepository {

	Optional<ImdbTitleAkas> findById(final String id);

	ImdbTitleAkas save(final ImdbTitleAkas imdbTitleAkas);

	void deleteById(final String id);

	Optional<ImdbTitleAkas> findByTitleId(final String titleId);

}
