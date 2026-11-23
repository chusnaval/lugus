package lugus.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.films.Edition;

public interface JpaEditionRepository extends EditionRepository, JpaRepository<Edition, Integer>, JpaSpecificationExecutor<Edition> {

}
