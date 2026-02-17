package lugus.infrastructure.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.core.LocationType;
import lugus.repository.core.LocationTypeRepository;

public interface JpaLocationTypeRepository extends LocationTypeRepository, JpaRepository<LocationType, Integer> {

}