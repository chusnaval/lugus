package lugus.service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.Fuente;
import lugus.repository.core.FuenteRepository;

@Service
@RequiredArgsConstructor
public class FuenteService {

	private final FuenteRepository fuenteRepository;

	public Optional<Fuente> findById(Integer id) {
		return fuenteRepository.findById(id);
	}

	public List<Fuente> findAll() {
		return fuenteRepository.findAll();
	}

	public List<Fuente> findBySuggestIsNotNull() {
		return fuenteRepository.findBySuggestIsNotNull();
	}
}
