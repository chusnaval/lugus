package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.FilmStaff;



public interface FilmStaffRepository {

	List<FilmStaff> findAll();

	Optional<FilmStaff> findById(final Integer id);

	FilmStaff save(final FilmStaff staff);

	void deleteById(final Integer id);

}
