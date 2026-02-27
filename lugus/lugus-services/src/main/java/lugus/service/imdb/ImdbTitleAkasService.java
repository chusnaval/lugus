package lugus.service.imdb;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.repository.imdb.ImdbTitleAkasRepository;

@Service
@RequiredArgsConstructor
public class ImdbTitleAkasService {

	private final ImdbTitleAkasRepository imdbTitleAkasRepository;
	
	public Optional<ImdbTitleAkas> findByTitleId(final String titleId){
		return imdbTitleAkasRepository.findByTitleId(titleId);
	}
}
