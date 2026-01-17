package lugus.service.people;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.people.PeliculasPersonalRepository;

@Service
@RequiredArgsConstructor
public class PeliculasPersonalService {
	
	private final PeliculasPersonalRepository peliculasPersonalRepository;

}
