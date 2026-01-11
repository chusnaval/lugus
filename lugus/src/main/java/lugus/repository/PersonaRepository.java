package lugus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Persona;



public interface PersonaRepository extends JpaRepository<Persona, Integer> {
	
	

}
