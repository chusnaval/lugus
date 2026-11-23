package lugus.service.people;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lugus.repository.people.InsertSeriesStaffDataRepository;



@Service
@RequiredArgsConstructor
public class InsertPersonalSeriesDataService {

	private final InsertSeriesStaffDataRepository repo;
	
	
	public void insert(final int serieId, final String imdb) {
		repo.insert(serieId, imdb);
	}
   
}
