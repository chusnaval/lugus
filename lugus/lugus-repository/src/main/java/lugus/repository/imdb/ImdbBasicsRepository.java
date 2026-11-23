package lugus.repository.imdb;

import java.util.List;

import lugus.model.imdb.ImdbBasics;

public interface ImdbBasicsRepository {

	List<ImdbBasics> findAll();
	
	List<ImdbBasics> findByTitleContainingIgnoreCaseAndRegionAndLanguage(final String title, final String region, final String language);

	ImdbBasics findByTitleid(final String id);
}
