package lugus.repository.people;

import java.util.List;
import java.util.Optional;

import lugus.model.people.SeriesStaff;



public interface SeriesStaffRepository {

	List<SeriesStaff> findAll();

	Optional<SeriesStaff> findById(final Integer id);

	SeriesStaff save(final SeriesStaff staff);

	void deleteById(final Integer id);

}
