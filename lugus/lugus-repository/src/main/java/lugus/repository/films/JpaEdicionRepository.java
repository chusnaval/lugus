package lugus.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.films.Edition;

public interface JpaEdicionRepository extends EdicionRepository, JpaRepository<Edition, Integer>, JpaSpecificationExecutor<Edition> {

}
