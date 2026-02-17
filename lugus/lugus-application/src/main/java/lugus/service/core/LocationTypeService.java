package lugus.service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.LocationType;
import lugus.repository.core.LocationTypeRepository;

@Service
@RequiredArgsConstructor
public class LocationTypeService {

	private final LocationTypeRepository repository;

	public Optional<LocationType> findById(int id) {
		return repository.findById(id);
	}

	public List<LocationType> findAll() {
		return repository.findAll();
	}
}
