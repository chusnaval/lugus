package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Persona;
import lugus.repository.people.PersonaRepository;

public interface JpaPersonaRepository extends PersonaRepository, JpaRepository<Persona, Integer> {

}