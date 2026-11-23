package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.Staff;



public interface PeliculasPersonalRepository {

	List<Staff> findAll();

	Optional<Staff> findById(Integer id);

	Staff save(Staff peliculasPersonal);

	void deleteById(Integer id);

}
