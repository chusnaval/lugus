package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.PeliculasPersonal;



public interface PeliculasPersonalRepository {

	List<PeliculasPersonal> findAll();

	Optional<PeliculasPersonal> findById(Integer id);

	PeliculasPersonal save(PeliculasPersonal peliculasPersonal);

	void deleteById(Integer id);

}
