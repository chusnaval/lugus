package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import lugus.model.core.Location;
import lugus.model.core.LocationType;

public interface LocationRepository {

	List<Location> findAll();

	List<Location> findAll(Sort sort);

	Optional<Location> findById(String id);

	Location save(Location location);

	void deleteById(String id);

	List<Location> findAllByLocationType(LocationType tipoObj, Sort sort);

	Page<Location> findAll(Specification<Location> spec, Pageable pageable);

	List<Location> findByCodigoNot(String code);

}
