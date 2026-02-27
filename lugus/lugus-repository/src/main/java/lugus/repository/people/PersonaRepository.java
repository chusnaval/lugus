package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Persona;



public interface PersonaRepository {

	List<Persona> findAll();

	Optional<Persona> findById(Integer id);

	Persona save(Persona persona);

	void deleteById(Integer id);

}
