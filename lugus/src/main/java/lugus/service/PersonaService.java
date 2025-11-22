package lugus.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.PersonaRepository;
@Service
@RequiredArgsConstructor
public class PersonaService {

	@SuppressWarnings("unused")
	private final PersonaRepository personaRepository;
}
