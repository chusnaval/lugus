package lugus.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.ImdbTitleBasics;
import lugus.repository.ImdbTitleBasicsRepository;
@Service
@RequiredArgsConstructor
public class ImdbTitleBasicsService {

	private final ImdbTitleBasicsRepository imdbTitleBasicsRepository;

	public Optional<ImdbTitleBasics> findById(String tconst) {
		return imdbTitleBasicsRepository.findById(tconst);
	}
}
