package lugus.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.TiposUbicacion;
import lugus.repository.TiposUbicacionRepository;

@Service
@RequiredArgsConstructor
public class TiposUbicacionService {

	private final TiposUbicacionRepository repository;

	public Optional<TiposUbicacion> findById(int id) {
		return repository.findById(id);
	}
}
