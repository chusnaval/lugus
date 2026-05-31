package lugus.repository.imdb;

import java.util.List;

import lugus.model.imdb.ImdbBasics;

public interface ImdbBasicsRepository {

	List<ImdbBasics> findAll();
	
	List<ImdbBasics> findByTitleContainingIgnoreCaseAndRegionAndLanguage(String title, String region, String language);

	ImdbBasics findByTitleid(final String id);
}
