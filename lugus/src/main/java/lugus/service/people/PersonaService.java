package lugus.service.people;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.people.Persona;
import lugus.repository.people.PersonaRepository;
@Service
@RequiredArgsConstructor
public class PersonaService {

	private final PersonaRepository personaRepository;

	public Optional<Persona> findById(Integer id) {
		return personaRepository.findById(id);
	}
}
