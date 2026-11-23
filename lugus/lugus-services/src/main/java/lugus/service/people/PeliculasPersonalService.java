package lugus.service.people;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.people.FilmStaffRepository;


@Service
@RequiredArgsConstructor
public class PeliculasPersonalService {
	
	@SuppressWarnings("unused")
	private final FilmStaffRepository peliculasPersonalRepository;

}
