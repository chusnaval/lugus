package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Director;
import lugus.repository.people.DirectorRepository;

public interface JpaDirectorRepository extends DirectorRepository, JpaRepository<Director, Integer> {

}