package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.People;
import lugus.repository.people.PeopleRepository;

public interface JpaPeopleRepository extends PeopleRepository, JpaRepository<People, Integer> {

}