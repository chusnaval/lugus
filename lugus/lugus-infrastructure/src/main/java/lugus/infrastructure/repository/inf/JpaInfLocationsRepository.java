package lugus.infrastructure.repository.inf;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.inf.InfLocations;
import lugus.model.inf.InfLocationsId;
import lugus.repository.inf.InfLocationsRepository;

public interface JpaInfLocationsRepository extends InfLocationsRepository, JpaRepository<InfLocations, InfLocationsId> {

}