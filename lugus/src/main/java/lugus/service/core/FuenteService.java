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

	private final FuenteRepository repository;

	public Optional<Fuente> findById(Integer id) {
		return repository.findById(id);
	}

	public List<Fuente> findAll() {
		return repository.findAll();
	}

	public List<Fuente> findBySuggestIsNotNull() {
		return repository.findBySuggestIsNotNull();
	}

	public Fuente save(Fuente source) {
		return repository.save(source);
	}

	public void deleteById(Integer id) {
		repository.deleteById(id);
	}
}
