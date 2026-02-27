package lugus.service.imdb;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbDirector;
import lugus.repository.imdb.ImdbDirectorRepository;

@Service
@RequiredArgsConstructor
public class ImdbDirectorService {

	private final ImdbDirectorRepository imdbDirectorRepository;
	
	List<ImdbDirector> findByPrimaryName(String name){
		return imdbDirectorRepository.findByPrimaryName(name);
	}
}
