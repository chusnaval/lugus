package lugus.infrastructure.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.repository.core.StatusRepository;

public interface JpaEstadoRepository extends StatusRepository, JpaRepository<lugus.model.core.Status, Integer> {

}
