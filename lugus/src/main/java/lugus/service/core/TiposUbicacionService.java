package lugus.service.core;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.TiposUbicacion;
import lugus.repository.core.TiposUbicacionRepository;

@Service
@RequiredArgsConstructor
public class TiposUbicacionService {

	private final TiposUbicacionRepository repository;

	public Optional<TiposUbicacion> findById(int id) {
		return repository.findById(id);
	}
}
