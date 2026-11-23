package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.SeriesStaff;
import lugus.repository.people.SeriesStaffRepository;


public interface JpaSeriesStaffRepository extends SeriesStaffRepository, JpaRepository<SeriesStaff, Integer> {

}