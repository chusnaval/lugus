package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.Source;

public interface SourceRepository {

	Optional<Source> findById(Integer id);

	List<Source> findAll();

	Source save(Source source);

	void deleteById(Integer id);

	List<Source> findBySuggestIsNotNull();

}
