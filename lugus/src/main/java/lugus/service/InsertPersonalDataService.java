package lugus.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lugus.repository.InsertPersonalDataRepository;

@Service
@RequiredArgsConstructor
public class InsertPersonalDataService {

	private final InsertPersonalDataRepository repo;
	
	
	public void insert(final int peliculaId, final String imdb) {
		repo.insert(peliculaId, imdb);
	}
   
}
