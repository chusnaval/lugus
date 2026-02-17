package lugus.service.imdb;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.repository.imdb.ImdbTitleBasicsRepository;
@Service
@RequiredArgsConstructor
public class ImdbTitleBasicsService {

	private final ImdbTitleBasicsRepository imdbTitleBasicsRepository;

	public Optional<ImdbTitleBasics> findById(String tconst) {
		return imdbTitleBasicsRepository.findById(tconst);
	}
}
