package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.FilmStaff;
import lugus.repository.people.FilmStaffRepository;


public interface JpaFilmStaffRepository extends FilmStaffRepository, JpaRepository<FilmStaff, Integer> {

}