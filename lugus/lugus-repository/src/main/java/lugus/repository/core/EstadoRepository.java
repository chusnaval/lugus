package lugus.repository.core;

import java.util.List;
import java.util.Optional;

import lugus.model.core.Status;

public interface EstadoRepository {

	public Optional<Status> findById(Integer estadoCodigo);

	public List<Status> findAll();

}
