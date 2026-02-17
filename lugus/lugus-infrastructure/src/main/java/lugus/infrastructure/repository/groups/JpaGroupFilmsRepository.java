package lugus.infrastructure.repository.groups;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.groups.GroupFilms;
import lugus.repository.groups.GroupFilmsRepository;

public interface JpaGroupFilmsRepository extends GroupFilmsRepository, JpaRepository<GroupFilms, Integer> {

}