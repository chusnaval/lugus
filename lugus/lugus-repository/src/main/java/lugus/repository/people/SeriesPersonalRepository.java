package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.SeriesPersonal;



public interface SeriesPersonalRepository {

	List<SeriesPersonal> findAll();

	Optional<SeriesPersonal> findById(final Integer id);

	SeriesPersonal save(final SeriesPersonal peliculasPersonal);

	void deleteById(final Integer id);

}
