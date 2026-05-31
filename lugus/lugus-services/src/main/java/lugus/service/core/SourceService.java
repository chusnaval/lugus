package lugus.service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import lugus.model.core.Source;
import lugus.repository.core.SourceRepository;

@Service
@RequiredArgsConstructor
public class SourceService {

	private final SourceRepository repository;

	public Optional<Source> findById(Integer id) {
		return repository.findById(id);
	}

	public List<Source> findAll() {
		return repository.findAll();
	}

	public List<Source> findBySuggestIsNotNull() {
		return repository.findBySuggestIsNotNull();
	}

	public Source save(Source source) {
		return repository.save(source);
	}

	public void deleteById(Integer id) {
		repository.deleteById(id);
	}

	public Integer calcularIdSource(String url) {
		List<Source> sources = findAll();
		int id = 10;
		Optional<Source> source = sources.stream()
				.filter(s -> s.getSuggest() != null && url != null && url.contains(s.getSuggest())).findFirst();
		if (source.isPresent()) {
			id = source.get().getId();
		}
		return id;
	}
}
