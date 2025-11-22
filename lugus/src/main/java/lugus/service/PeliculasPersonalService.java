package lugus.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.PeliculasPersonalRepository;

@Service
@RequiredArgsConstructor
public class PeliculasPersonalService {
	
	private final PeliculasPersonalRepository peliculasPersonalRepository;

}
