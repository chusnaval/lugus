package lugus.infrastructure.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.repository.core.EstadoRepository;

public interface JpaEstadoRepository extends EstadoRepository, JpaRepository<lugus.model.core.Estado, Integer> {

}
