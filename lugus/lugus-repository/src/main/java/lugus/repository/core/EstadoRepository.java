package lugus.repository.core;

import java.util.Optional;

import lugus.model.core.Estado;

public interface EstadoRepository {

	public Optional<Estado> findById(Integer estadoCodigo);

}
