package lugus.repository.imdb;

import java.util.List;
import java.util.Optional;

import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.imdb.ImdbTitlePrincipalsId;

public interface ImdbTitlePrincipalsRepository {

	Optional<ImdbTitlePrincipals> findById(ImdbTitlePrincipalsId id);

	ImdbTitlePrincipals save(ImdbTitlePrincipals imdbTitlePrincipals);

	void deleteById(ImdbTitlePrincipalsId id);

	List<ImdbTitlePrincipals> findAllByIdNconst(String nConst);

}
