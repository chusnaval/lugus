package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Actor;



public interface ActorRepository {

	List<Actor> findAll();

	Optional<Actor> findById(Integer id);

	Actor save(Actor actor);

	void deleteById(Integer id);

	List<Actor> findByPeliculaIdOrderByOrdenAsc(int id);

}
