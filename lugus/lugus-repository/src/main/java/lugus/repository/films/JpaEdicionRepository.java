package lugus.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.films.Edicion;

public interface JpaEdicionRepository extends EdicionRepository, JpaRepository<Edicion, Integer>, JpaSpecificationExecutor<Edicion> {

}
