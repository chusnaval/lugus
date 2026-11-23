package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.Status;

public interface StatusRepository {

	public Optional<Status> findById(final int statusCode);

	public List<Status> findAll();

}
