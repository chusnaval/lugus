package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.SeriesPersonal;



public interface SeriesPersonalRepository {

	List<SeriesPersonal> findAll();

	Optional<SeriesPersonal> findById(Integer id);

	SeriesPersonal save(SeriesPersonal peliculasPersonal);

	void deleteById(Integer id);

}
