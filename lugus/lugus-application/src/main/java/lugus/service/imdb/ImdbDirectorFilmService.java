package lugus.service.imdb;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbDirectorFilm;
import lugus.repository.imdb.ImdbDirectorFilmRepository;

@Service
@RequiredArgsConstructor
public class ImdbDirectorFilmService {

	private final ImdbDirectorFilmRepository imdbDirectorFilmRepository;
	
	public List<ImdbDirectorFilm> findByTitle(final String title){
		return imdbDirectorFilmRepository.findByTitleContainingIgnoreCase(title);
	}
}
