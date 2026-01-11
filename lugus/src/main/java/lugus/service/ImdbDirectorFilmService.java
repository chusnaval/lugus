package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbDirectorFilm;
import lugus.repository.ImdbDirectorFilmRepository;

@Service
@RequiredArgsConstructor
public class ImdbDirectorFilmService {

	private final ImdbDirectorFilmRepository imdbDirectorFilmRepository;
	
	public List<ImdbDirectorFilm> findByTitle(final String title){
		return imdbDirectorFilmRepository.findByTitleContainingIgnoreCase(title);
	}
}
