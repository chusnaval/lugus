package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.People;



public interface PeopleRepository {

	List<People> findAll();

	Optional<People> findById(final Integer id);

	People save(People persona);

	void deleteById(Integer id);

}
