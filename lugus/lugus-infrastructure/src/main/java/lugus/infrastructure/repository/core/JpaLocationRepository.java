package lugus.infrastructure.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.core.Location;
import lugus.repository.core.LocationRepository;

public interface JpaLocationRepository extends LocationRepository, JpaRepository<Location, String>, JpaSpecificationExecutor<Location> {

}