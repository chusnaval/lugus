package lugus.service.people;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.people.People;
import lugus.repository.people.PeopleRepository;

@Service
@RequiredArgsConstructor
public class PersonaService {

	private final PeopleRepository personaRepository;

	public Optional<People> findById(Integer id) {
		return personaRepository.findById(id);
	}
}
