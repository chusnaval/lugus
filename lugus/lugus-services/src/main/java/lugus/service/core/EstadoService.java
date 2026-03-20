package lugus.service.core;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.Estado;
import lugus.repository.core.EstadoRepository;

@Service
@RequiredArgsConstructor
public class EstadoService {

	private final EstadoRepository estadoRepo;

	public Optional<Estado> findEstadoById(Integer estadoCodigo) {
		
		return estadoRepo.findById(estadoCodigo);
	}
}
