package lugus.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.people.Persona;
import lugus.repository.PersonaRepository;
@Service
@RequiredArgsConstructor
public class PersonaService {

	private final PersonaRepository personaRepository;

	public Optional<Persona> findById(Integer id) {
		return personaRepository.findById(id);
	}
}
