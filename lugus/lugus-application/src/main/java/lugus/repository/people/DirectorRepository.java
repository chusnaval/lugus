package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Director;


public interface DirectorRepository {

	List<Director> findAll();

	Optional<Director> findById(Integer id);

	Director save(Director director);

	void deleteById(Integer id);

	List<Director> findByPeliculaId(int id);

}
