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

	List<Location> findAll(final Sort sort);

	Optional<Location> findById(final String id);

	Location save(final Location location);

	void deleteById(final String id);

	List<Location> findAllByLocationType(final LocationType tipoObj, final Sort sort);

	Page<Location> findAll(final Specification<Location> spec, final Pageable pageable);

	List<Location> findByCodeNot(final String code);

}
