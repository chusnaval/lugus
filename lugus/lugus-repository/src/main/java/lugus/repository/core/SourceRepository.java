package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.Source;

public interface SourceRepository {

	Optional<Source> findById(final Integer id);

	List<Source> findAll();

	Source save(final Source source);

	void deleteById(final Integer id);

	List<Source> findBySuggestIsNotNull();

}
