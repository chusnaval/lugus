package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Director;


public interface DirectorRepository {

	List<Director> findAll();

	Optional<Director> findById(final Integer id);

	Director save(final Director director);

	void deleteById(final Integer id);

	List<Director> findByFilmId(final int id);

}
