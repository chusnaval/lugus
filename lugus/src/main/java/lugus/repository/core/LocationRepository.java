package lugus.repository.core;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.core.Location;
import lugus.model.core.LocationType;

public interface LocationRepository extends JpaRepository<Location, String> {

	List<Location> findAll();

	List<Location> findAllByLocationType(LocationType tipoObj, Sort sort);

	Page<Location> findAll(Specification<Location> spec, Pageable pageable);

}
