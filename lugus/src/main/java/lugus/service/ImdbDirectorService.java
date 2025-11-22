package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.ImdbDirector;
import lugus.repository.ImdbDirectorRepository;

@Service
@RequiredArgsConstructor
public class ImdbDirectorService {

	private final ImdbDirectorRepository imdbDirectorRepository;
	
	List<ImdbDirector> findByPrimaryName(String name){
		return imdbDirectorRepository.findByPrimaryName(name);
	}
}
