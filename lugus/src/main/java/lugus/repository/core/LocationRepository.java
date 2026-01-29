package lugus.repository.core;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.core.Location;
import lugus.model.core.LocationType;

public interface LocationRepository extends JpaRepository<Location, String> {

	List<Location> findAll();

	List<Location> findAllByLocationType(LocationType tipoObj, Sort sort);

}
