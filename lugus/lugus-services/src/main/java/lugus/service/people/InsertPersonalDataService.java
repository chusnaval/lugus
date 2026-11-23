package lugus.service.people;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lugus.repository.people.InsertStaffDataRepository;



@Service
@RequiredArgsConstructor
public class InsertPersonalDataService {

	private final InsertStaffDataRepository repo;
	
	
	public void insert(final int peliculaId, final String imdb) {
		repo.insert(peliculaId, imdb);
	}
   
}
