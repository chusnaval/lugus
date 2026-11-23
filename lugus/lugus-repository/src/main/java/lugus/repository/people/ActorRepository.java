package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Actor;



public interface ActorRepository {

	List<Actor> findAll();

	Optional<Actor> findById(final Integer id);

	Actor save(final Actor actor);

	void deleteById(final Integer id);

	List<Actor> findByFilmIdOrderByOrderAsc(final int id);

}
