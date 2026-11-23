package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.imdb.ImdbTitlePrincipalsId;

public interface ImdbTitlePrincipalsRepository {

	Optional<ImdbTitlePrincipals> findById(final ImdbTitlePrincipalsId id);

	ImdbTitlePrincipals save(final ImdbTitlePrincipals imdbTitlePrincipals);

	void deleteById(final ImdbTitlePrincipalsId id);

	List<ImdbTitlePrincipals> findAllByIdNconst(final String nConst);

}
