package lugus.service.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.people.PeliculasPersonalRepository;

@Service
@RequiredArgsConstructor
public class PeliculasPersonalService {
	
	@SuppressWarnings("unused")
	@Autowired
	private final PeliculasPersonalRepository peliculasPersonalRepository;

}
