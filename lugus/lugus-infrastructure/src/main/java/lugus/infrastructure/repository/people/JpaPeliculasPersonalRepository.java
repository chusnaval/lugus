package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Staff;
import lugus.repository.people.PeliculasPersonalRepository;


public interface JpaPeliculasPersonalRepository extends PeliculasPersonalRepository, JpaRepository<Staff, Integer> {

}