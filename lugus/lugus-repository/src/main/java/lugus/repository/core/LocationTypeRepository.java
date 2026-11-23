package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.LocationType;

public interface LocationTypeRepository {

	Optional<LocationType> findById(final int id);

	List<LocationType> findAll();

	LocationType save(final LocationType obj);

	void deleteById(final int id);

}
