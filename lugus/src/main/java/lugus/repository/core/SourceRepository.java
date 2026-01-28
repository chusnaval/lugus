package lugus.repository.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.core.Source;

public interface SourceRepository extends JpaRepository<Source, Integer> {

	List<Source> findBySuggestIsNotNull();

}
