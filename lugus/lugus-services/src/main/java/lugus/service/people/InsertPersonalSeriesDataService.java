package lugus.service.people;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lugus.repository.people.InsertStaffSeriesDataRepository;



@Service
@RequiredArgsConstructor
public class InsertPersonalSeriesDataService {

	private final InsertStaffSeriesDataRepository repo;
	
	
	public void insert(final int serieId, final String imdb) {
		repo.insert(serieId, imdb);
	}
   
}
