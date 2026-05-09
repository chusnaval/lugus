package lugus.service.imdb;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbBasics;
import lugus.repository.imdb.ImdbBasicsRepository;

@Service
@RequiredArgsConstructor
public class ImdbBasicsService {
	
	private final ImdbBasicsRepository imdbBasicsRepository;

	public List<ImdbBasics> findByTitleAndRegion(final String title, final String region) {
		return imdbBasicsRepository.findByTitleContainingIgnoreCaseAndRegionAndLanguage(title, region, null);
	}
}
