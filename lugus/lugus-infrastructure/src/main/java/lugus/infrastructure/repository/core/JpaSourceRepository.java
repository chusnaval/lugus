package lugus.infrastructure.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.core.Source;
import lugus.repository.core.SourceRepository;

public interface JpaSourceRepository extends SourceRepository, JpaRepository<Source, Integer> {

}