package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.LocationType;

public interface LocationTypeRepository {

	Optional<LocationType> findById(Integer id);

	List<LocationType> findAll();

}
