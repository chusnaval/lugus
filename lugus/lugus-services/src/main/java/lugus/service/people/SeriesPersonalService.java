package lugus.service.people;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.people.SeriesStaffRepository;


@Service
@RequiredArgsConstructor
public class SeriesPersonalService {
	
	@SuppressWarnings("unused")
	private final SeriesStaffRepository seriesPersonalRepository;

}
